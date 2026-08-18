package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Unique authenticated user ID (e.g. "user_101", UUID)
    val email: String,
    val displayName: String,
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
