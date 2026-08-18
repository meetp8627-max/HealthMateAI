package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder_logs")
data class ReminderLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val reminderId: Long,
    val medicineId: Long,
    val medicineName: String,
    val dosageText: String = "",
    val scheduledTime: String = "08:00", // HH:mm
    val dateString: String = "", // YYYY-MM-DD
    val status: String = "upcoming", // upcoming, taken, skipped, missed
    val completedAt: Long? = null
)
