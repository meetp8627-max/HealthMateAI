package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicine_knowledge")
data class MedicineKnowledgeEntity(
    @PrimaryKey val medicineId: Long,
    val normalizedName: String,
    val brandName: String = "",
    val activeIngredients: List<String> = emptyList(),
    val medicineType: String, // e.g. "Antibiotic", "Pain Reliever (NSAID)", "Antihistamine"
    val shortSummary: String, // 1-3 sentences for "What is it used for?"
    val simplifiedSummary: String? = null, // Simplified rephrasing
    val mechanism: String, // "How does it work?"
    val commonUses: List<String>, // List of approved/general uses
    val importantSafetyInfo: List<String>, // Key precautions, allergy/age warnings
    val commonSideEffects: List<String>, // Common, source-supported effects
    val isCombination: Boolean = false,
    val sourceName: String = "MedlinePlus (National Library of Medicine)",
    val sourceUrl: String = "https://medlineplus.gov/druginformation.html",
    val sourceUpdatedAt: String = "August 2026",
    val fetchedAt: Long = System.currentTimeMillis()
)
