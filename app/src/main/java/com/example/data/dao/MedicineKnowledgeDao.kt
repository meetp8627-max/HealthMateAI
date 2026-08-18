package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MedicineKnowledgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineKnowledgeDao {
    @Query("SELECT * FROM medicine_knowledge WHERE medicineId = :medicineId LIMIT 1")
    fun getKnowledgeByMedicineIdFlow(medicineId: Long): Flow<MedicineKnowledgeEntity?>

    @Query("SELECT * FROM medicine_knowledge WHERE medicineId = :medicineId LIMIT 1")
    suspend fun getKnowledgeByMedicineId(medicineId: Long): MedicineKnowledgeEntity?

    @Query("SELECT * FROM medicine_knowledge WHERE LOWER(normalizedName) = LOWER(:normalizedName) LIMIT 1")
    suspend fun getKnowledgeByNormalizedName(normalizedName: String): MedicineKnowledgeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledge(knowledge: MedicineKnowledgeEntity)

    @Update
    suspend fun updateKnowledge(knowledge: MedicineKnowledgeEntity)

    @Query("DELETE FROM medicine_knowledge WHERE medicineId = :medicineId")
    suspend fun deleteKnowledgeByMedicineId(medicineId: Long)

    @Query("DELETE FROM medicine_knowledge")
    suspend fun deleteAllKnowledge()
}
