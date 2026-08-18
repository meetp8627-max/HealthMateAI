package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history WHERE userId = :userId ORDER BY scannedAt DESC")
    fun getAllScanHistory(userId: String): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanHistory(scan: ScanHistoryEntity): Long

    @Query("DELETE FROM scan_history WHERE userId = :userId AND id = :id")
    suspend fun deleteScanById(userId: String, id: Long)

    @Query("DELETE FROM scan_history WHERE userId = :userId")
    suspend fun deleteAllScanHistoryForUser(userId: String)

    @Query("DELETE FROM scan_history")
    suspend fun deleteAllScanHistory()
}
