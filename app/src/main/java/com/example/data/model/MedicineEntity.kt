package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String = "",
    val name: String,
    val brandName: String = "",
    val activeIngredients: List<String> = emptyList(),
    val strength: String = "",
    val form: String = "Tablet", // Tablet, Capsule, Syrup, Cream, Drops, etc.
    val manufacturer: String = "",
    val expiryDate: String = "", // e.g. "12/2027"
    val batchNumber: String = "",
    val quantity: String = "",
    val notes: String = "",
    val scanImageUri: String? = null,
    val confidence: String = "High", // High, Medium, Low
    val verified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
