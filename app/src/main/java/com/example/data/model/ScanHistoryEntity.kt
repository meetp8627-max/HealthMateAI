package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val scannedAt: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val detectedName: String = "",
    val brandName: String = "",
    val strength: String = "",
    val form: String = "",
    val confidence: String = "High",
    val verified: Boolean = false,
    val rawResponseJson: String = ""
)
