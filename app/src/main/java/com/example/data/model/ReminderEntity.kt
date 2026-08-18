package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val medicineId: Long,
    val medicineName: String,
    val scheduleType: String = "daily", // "once", "daily", "weekly", "monthly", "custom"
    val startDate: String = "", // YYYY-MM-DD
    val endDate: String? = null, // YYYY-MM-DD or null
    val selectedDates: List<String> = emptyList(), // list of YYYY-MM-DD for custom or once mode
    val weekdays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val dayOfMonth: Int? = null, // 1..31
    val times: List<String> = listOf("08:00"), // list of HH:mm 24-hr format strings
    val dosageText: String = "",
    val notes: String = "",
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

