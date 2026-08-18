package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicines WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllMedicines(userId: String): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getMedicineById(userId: String, id: Long): MedicineEntity?

    @Query("SELECT * FROM medicines WHERE userId = :userId AND (LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(brandName) LIKE '%' || LOWER(:query) || '%')")
    fun searchMedicines(userId: String, query: String): Flow<List<MedicineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Query("DELETE FROM medicines WHERE userId = :userId AND id = :id")
    suspend fun deleteMedicineById(userId: String, id: Long)

    @Query("DELETE FROM medicines WHERE userId = :userId")
    suspend fun deleteAllMedicinesForUser(userId: String)

    @Query("DELETE FROM medicines")
    suspend fun deleteAllMedicines()
}
