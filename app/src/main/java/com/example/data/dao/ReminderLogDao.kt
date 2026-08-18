package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReminderLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderLogDao {
    @Query("SELECT * FROM reminder_logs WHERE userId = :userId AND dateString = :date ORDER BY scheduledTime ASC")
    fun getLogsForDate(userId: String, date: String): Flow<List<ReminderLogEntity>>

    @Query("SELECT * FROM reminder_logs WHERE userId = :userId ORDER BY id DESC LIMIT 100")
    fun getAllLogs(userId: String): Flow<List<ReminderLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ReminderLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<ReminderLogEntity>)

    @Update
    suspend fun updateLog(log: ReminderLogEntity)

    @Query("UPDATE reminder_logs SET status = :status, completedAt = :completedAt WHERE userId = :userId AND id = :id")
    suspend fun updateLogStatus(userId: String, id: Long, status: String, completedAt: Long?)

    @Query("DELETE FROM reminder_logs WHERE userId = :userId AND id = :id")
    suspend fun deleteLogById(userId: String, id: Long)

    @Query("DELETE FROM reminder_logs WHERE userId = :userId")
    suspend fun deleteAllLogsForUser(userId: String)

    @Query("DELETE FROM reminder_logs")
    suspend fun deleteAllLogs()
}
