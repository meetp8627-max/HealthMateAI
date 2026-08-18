package com.example.data.repository

import com.example.data.ai.AiAssistantService
import com.example.data.ai.AiResult
import com.example.data.dao.MedicineKnowledgeDao
import com.example.data.knowledge.MedicationKnowledgeBase
import com.example.data.model.MedicineEntity
import com.example.data.model.MedicineKnowledgeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MedicationKnowledgeRepository(
    private val knowledgeDao: MedicineKnowledgeDao,
    private val aiAssistantService: AiAssistantService = AiAssistantService()
) {

    fun getKnowledgeFlow(medicineId: Long): Flow<MedicineKnowledgeEntity?> {
        return knowledgeDao.getKnowledgeByMedicineIdFlow(medicineId)
    }

    suspend fun getOrFetchKnowledge(medicine: MedicineEntity): MedicineKnowledgeEntity? {
        // 1. Check local Room database cache
        val cached = knowledgeDao.getKnowledgeByMedicineId(medicine.id)
        if (cached != null) {
            return cached
        }

        // 2. Identify and match against trusted database
        val searchCandidates = listOf(
            medicine.name,
            medicine.brandName
        ).filter { it.isNotBlank() }

        var matched = MedicationKnowledgeBase.findMedication(
            query = searchCandidates.joinToString(" "),
            activeIngredients = medicine.activeIngredients
        )

        // Try single names if combined string did not match
        if (matched == null && medicine.name.isNotBlank()) {
            matched = MedicationKnowledgeBase.findMedication(
                query = medicine.name,
                activeIngredients = medicine.activeIngredients
            )
        }
        if (matched == null && medicine.brandName.isNotBlank()) {
            matched = MedicationKnowledgeBase.findMedication(
                query = medicine.brandName,
                activeIngredients = medicine.activeIngredients
            )
        }

        if (matched == null) {
            return null
        }

        // 3. Construct knowledge entity
        val entity = MedicineKnowledgeEntity(
            medicineId = medicine.id,
            normalizedName = matched.genericName,
            brandName = medicine.brandName.ifBlank { matched.brandNames.firstOrNull() ?: "" },
            activeIngredients = if (matched.activeIngredients.isNotEmpty()) matched.activeIngredients else medicine.activeIngredients,
            medicineType = matched.medicineType,
            shortSummary = matched.shortSummary,
            mechanism = matched.mechanism,
            commonUses = matched.commonUses,
            importantSafetyInfo = matched.importantSafetyInfo,
            commonSideEffects = matched.commonSideEffects,
            isCombination = matched.isCombination || (matched.activeIngredients.size > 1),
            sourceName = matched.sourceName,
            sourceUrl = matched.sourceUrl,
            sourceUpdatedAt = matched.sourceUpdatedAt,
            fetchedAt = System.currentTimeMillis()
        )

        // 4. Save to cache
        knowledgeDao.insertKnowledge(entity)
        return entity
    }

    suspend fun simplifyExplanation(medicineId: Long, currentExplanation: String): String? {
        val prompt = """
            You are a medical communications specialist.
            Rewrite the following VERIFIED medication summary in 1-2 simple, friendly sentences using plain language (around 5th-grade reading level).
            
            RULES:
            - ONLY rephrase the facts provided below.
            - Do NOT add new medical claims, diagnoses, or dosage instructions.
            - Keep it encouraging, objective, and clear.
            
            Verified text to simplify:
            "$currentExplanation"
        """.trimIndent()

        return when (val result = aiAssistantService.getHealthAssistantResponse(prompt)) {
            is AiResult.Success -> {
                val simplified = result.data.trim().removeSurrounding("\"")
                val existing = knowledgeDao.getKnowledgeByMedicineId(medicineId)
                if (existing != null) {
                    val updated = existing.copy(simplifiedSummary = simplified)
                    knowledgeDao.updateKnowledge(updated)
                }
                simplified
            }
            else -> null // Gracefully fall back to original source without showing errors
        }
    }
}
