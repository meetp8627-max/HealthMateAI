package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.dao.MedicineDao
import com.example.data.dao.MedicineKnowledgeDao
import com.example.data.dao.ReminderDao
import com.example.data.dao.ReminderLogDao
import com.example.data.dao.ScanHistoryDao
import com.example.data.dao.UserDao
import com.example.data.model.Converters
import com.example.data.model.MedicineEntity
import com.example.data.model.MedicineKnowledgeEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.ScanHistoryEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        MedicineEntity::class,
        MedicineKnowledgeEntity::class,
        ReminderEntity::class,
        ReminderLogEntity::class,
        ScanHistoryEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicineDao(): MedicineDao
    abstract fun medicineKnowledgeDao(): MedicineKnowledgeDao
    abstract fun reminderDao(): ReminderDao
    abstract fun reminderLogDao(): ReminderLogDao
    abstract fun scanHistoryDao(): ScanHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
