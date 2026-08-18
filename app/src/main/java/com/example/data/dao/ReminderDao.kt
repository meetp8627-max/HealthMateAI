package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId AND enabled = 1 ORDER BY id DESC")
    fun getAllActiveReminders(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND medicineId = :medicineId")
    fun getRemindersForMedicine(userId: String, medicineId: Long): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND id = :id LIMIT 1")
    suspend fun getReminderById(userId: String, id: Long): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE userId = :userId AND id = :id")
    suspend fun deleteReminderById(userId: String, id: Long)

    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteAllRemindersForUser(userId: String)

    @Query("DELETE FROM reminders")
    suspend fun deleteAllReminders()
}
