package com.example.data.model

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class Converters {
    private val moshi = Moshi.Builder().build()
    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.let { stringListAdapter.toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) emptyList()
        else try {
            stringListAdapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
