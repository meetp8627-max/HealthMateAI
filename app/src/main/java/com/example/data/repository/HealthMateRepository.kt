package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.ai.AiResult
import com.example.data.ai.AiAssistantService
import com.example.data.ai.GeminiVisionService
import com.example.data.ai.MedicineScanResult
import com.example.data.dao.MedicineDao
import com.example.data.dao.MedicineKnowledgeDao
import com.example.data.dao.ReminderDao
import com.example.data.dao.ReminderLogDao
import com.example.data.dao.ScanHistoryDao
import com.example.data.dao.UserDao
import com.example.data.model.MedicineEntity
import com.example.data.model.MedicineKnowledgeEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.ScanHistoryEntity
import com.example.data.model.UserEntity
import com.example.data.repository.MedicationKnowledgeRepository
import com.example.data.scheduler.ScheduleUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class HealthMateRepository(
    private val userDao: UserDao,
    private val medicineDao: MedicineDao,
    private val reminderDao: ReminderDao,
    private val reminderLogDao: ReminderLogDao,
    private val scanHistoryDao: ScanHistoryDao,
    private val knowledgeDao: MedicineKnowledgeDao? = null,
    private val visionService: GeminiVisionService = GeminiVisionService(),
    private val aiAssistantService: AiAssistantService = AiAssistantService()
) {
    val activeUserId = MutableStateFlow<String>("")

    private val medicationKnowledgeRepo: MedicationKnowledgeRepository? = knowledgeDao?.let {
        MedicationKnowledgeRepository(it, aiAssistantService)
    }

    val currentUser: Flow<UserEntity?> = activeUserId.flatMapLatest { id ->
        userDao.getUserByIdFlow(id)
    }

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()

    val allMedicines: Flow<List<MedicineEntity>> = activeUserId.flatMapLatest { id ->
        medicineDao.getAllMedicines(id)
    }

    val activeReminders: Flow<List<ReminderEntity>> = activeUserId.flatMapLatest { id ->
        reminderDao.getAllActiveReminders(id)
    }

    val scanHistory: Flow<List<ScanHistoryEntity>> = activeUserId.flatMapLatest { id ->
        scanHistoryDao.getAllScanHistory(id)
    }

    val allLogs: Flow<List<ReminderLogEntity>> = activeUserId.flatMapLatest { id ->
        reminderLogDao.getAllLogs(id)
    }

    suspend fun initUserSession() {
        val users = userDao.getAllUsers().first()
        val defaultUserId = "user_default"
        if (users.isEmpty()) {
            val defaultUser = UserEntity(
                id = defaultUserId,
                email = "alex@healthmate.ai",
                displayName = "Alex"
            )
            userDao.insertUser(defaultUser)
            activeUserId.value = defaultUserId

            // Seed sample medicines and reminders if not present
            seedInitialMedicinesAndReminders(defaultUserId)
        } else {
            if (activeUserId.value.isBlank()) {
                activeUserId.value = users.first().id
            }
            // If the active user has no medicines, seed starter sample data
            val existingMeds = medicineDao.getAllMedicines(activeUserId.value).first()
            if (existingMeds.isEmpty()) {
                seedInitialMedicinesAndReminders(activeUserId.value)
            }
        }
    }

    private suspend fun seedInitialMedicinesAndReminders(userId: String) {
        val todayStr = ScheduleUtils.getTodayDateString()

        // 1. Amoxicillin
        val med1Id = medicineDao.insertMedicine(
            MedicineEntity(
                userId = userId,
                name = "Amoxicillin",
                brandName = "Amoxil",
                activeIngredients = listOf("Amoxicillin trihydrate"),
                strength = "500 mg",
                form = "Capsule",
                manufacturer = "Sandoz",
                expiryDate = "10/2027",
                notes = "Take with a full glass of water after food.",
                confidence = "High",
                verified = true
            )
        )
        val rem1Id = reminderDao.insertReminder(
            ReminderEntity(
                userId = userId,
                medicineId = med1Id,
                medicineName = "Amoxicillin 500mg",
                scheduleType = "daily",
                startDate = todayStr,
                times = listOf("08:00", "20:00"),
                dosageText = "1 capsule (500mg)",
                notes = "Take after breakfast and dinner",
                enabled = true
            )
        )

        // 2. Vitamin D3
        val med2Id = medicineDao.insertMedicine(
            MedicineEntity(
                userId = userId,
                name = "Vitamin D3",
                brandName = "Nature Made",
                activeIngredients = listOf("Cholecalciferol"),
                strength = "1000 IU",
                form = "Softgel",
                manufacturer = "Nature Made",
                expiryDate = "05/2028",
                notes = "Supports bone health and immune system.",
                confidence = "High",
                verified = true
            )
        )
        reminderDao.insertReminder(
            ReminderEntity(
                userId = userId,
                medicineId = med2Id,
                medicineName = "Vitamin D3 1000 IU",
                scheduleType = "daily",
                startDate = todayStr,
                times = listOf("09:00"),
                dosageText = "1 softgel",
                notes = "Take with morning meal",
                enabled = true
            )
        )

        // 3. Paracetamol
        val med3Id = medicineDao.insertMedicine(
            MedicineEntity(
                userId = userId,
                name = "Paracetamol",
                brandName = "Panadol Advance",
                activeIngredients = listOf("Paracetamol"),
                strength = "500 mg",
                form = "Tablet",
                manufacturer = "GSK",
                expiryDate = "12/2027",
                notes = "For pain relief and fever reduction as needed.",
                confidence = "High",
                verified = true
            )
        )
        reminderDao.insertReminder(
            ReminderEntity(
                userId = userId,
                medicineId = med3Id,
                medicineName = "Paracetamol 500mg",
                scheduleType = "daily",
                startDate = todayStr,
                times = listOf("14:00"),
                dosageText = "1 tablet (500mg)",
                notes = "Take with water",
                enabled = true
            )
        )

        // Seed a recorded log for morning dose so adherence displays realistic progress
        reminderLogDao.insertLog(
            ReminderLogEntity(
                userId = userId,
                reminderId = rem1Id,
                medicineId = med1Id,
                medicineName = "Amoxicillin 500mg",
                dosageText = "1 capsule (500mg)",
                scheduledTime = "08:00",
                dateString = todayStr,
                status = "taken",
                completedAt = System.currentTimeMillis() - 3600000L
            )
        )
    }

    suspend fun resetPersonalization(userId: String) {
        val existing = userDao.getUserById(userId)
        if (existing != null) {
            val updated = existing.copy(displayName = "")
            userDao.updateUser(updated)
        }
    }

    fun switchUser(userId: String) {
        activeUserId.value = userId
    }

    suspend fun createOrSwitchUser(email: String, displayName: String): UserEntity {
        val existing = userDao.getUserByEmail(email)
        return if (existing != null) {
            val updated = existing.copy(
                displayName = if (displayName.isNotBlank()) displayName else existing.displayName
            )
            userDao.updateUser(updated)
            activeUserId.value = updated.id
            updated
        } else {
            val newUser = UserEntity(
                id = "user_" + UUID.randomUUID().toString().take(8),
                email = email.ifBlank { "user@example.com" },
                displayName = displayName.ifBlank { "" }
            )
            userDao.insertUser(newUser)
            activeUserId.value = newUser.id
            newUser
        }
    }

    suspend fun updateUserProfile(userId: String, newName: String, newEmail: String) {
        val existing = userDao.getUserById(userId)
        if (existing != null) {
            val updated = existing.copy(
                displayName = newName,
                email = newEmail
            )
            userDao.updateUser(updated)
        }
    }

    fun getTodayLogs(dateString: String = getTodayDateString()): Flow<List<ReminderLogEntity>> {
        return getOccurrencesForDate(dateString)
    }

    /**
     * Combines active reminders and saved log statuses for current user to generate full deterministic occurrences for any given date.
     */
    fun getOccurrencesForDate(dateString: String): Flow<List<ReminderLogEntity>> {
        return activeUserId.flatMapLatest { uid ->
            combine(
                reminderDao.getAllActiveReminders(uid),
                reminderLogDao.getLogsForDate(uid, dateString)
            ) { activeRemindersList, savedLogsForDate ->
                val logMap = savedLogsForDate.associateBy { "${it.reminderId}_${it.scheduledTime}_${it.dateString}" }
                val occurrences = mutableListOf<ReminderLogEntity>()

                for (reminder in activeRemindersList) {
                    if (ScheduleUtils.doesReminderOccurOnDate(reminder, dateString)) {
                        for (timeStr in reminder.times) {
                            val key = "${reminder.id}_${timeStr}_$dateString"
                            val existingLog = logMap[key]
                            if (existingLog != null) {
                                occurrences.add(existingLog)
                            } else {
                                occurrences.add(
                                    ReminderLogEntity(
                                        userId = uid,
                                        reminderId = reminder.id,
                                        medicineId = reminder.medicineId,
                                        medicineName = reminder.medicineName,
                                        dosageText = reminder.dosageText.ifEmpty { reminder.scheduleType },
                                        scheduledTime = timeStr,
                                        dateString = dateString,
                                        status = "upcoming"
                                    )
                                )
                            }
                        }
                    }
                }

                occurrences.sortedBy { it.scheduledTime }
            }
        }
    }

    suspend fun getMedicineById(id: Long): MedicineEntity? {
        val uid = activeUserId.value
        return medicineDao.getMedicineById(uid, id)
    }

    suspend fun getReminderById(id: Long): ReminderEntity? {
        val uid = activeUserId.value
        return reminderDao.getReminderById(uid, id)
    }

    fun searchMedicines(query: String): Flow<List<MedicineEntity>> {
        return medicineDao.searchMedicines(activeUserId.value, query)
    }

    suspend fun insertMedicine(medicine: MedicineEntity): Long {
        val uid = activeUserId.value
        return medicineDao.insertMedicine(medicine.copy(userId = uid))
    }

    suspend fun updateMedicine(medicine: MedicineEntity) {
        val uid = activeUserId.value
        medicineDao.updateMedicine(medicine.copy(userId = uid))
    }

    suspend fun deleteMedicine(id: Long) {
        val uid = activeUserId.value
        medicineDao.deleteMedicineById(uid, id)
    }

    suspend fun checkDuplicateReminder(
        medicineId: Long,
        times: List<String>,
        scheduleType: String,
        startDate: String
    ): ReminderEntity? {
        val uid = activeUserId.value
        val active = reminderDao.getAllActiveReminders(uid).first()
        return active.find { existing ->
            existing.medicineId == medicineId &&
                    existing.scheduleType == scheduleType &&
                    existing.times.sorted() == times.sorted() &&
                    existing.startDate == startDate
        }
    }

    suspend fun insertReminder(reminder: ReminderEntity): Long {
        val uid = activeUserId.value
        return reminderDao.insertReminder(reminder.copy(userId = uid))
    }

    suspend fun updateReminder(reminder: ReminderEntity) {
        val uid = activeUserId.value
        reminderDao.updateReminder(reminder.copy(userId = uid))
    }

    suspend fun deleteReminder(id: Long) {
        val uid = activeUserId.value
        reminderDao.deleteReminderById(uid, id)
    }

    suspend fun markOccurrenceStatus(
        log: ReminderLogEntity,
        newStatus: String
    ) {
        val uid = activeUserId.value
        val completedAt = if (newStatus == "taken") System.currentTimeMillis() else null
        if (log.id > 0) {
            reminderLogDao.updateLogStatus(uid, log.id, newStatus, completedAt)
        } else {
            val newLog = log.copy(userId = uid, status = newStatus, completedAt = completedAt)
            reminderLogDao.insertLog(newLog)
        }
    }

    suspend fun updateLogStatus(logId: Long, status: String) {
        val uid = activeUserId.value
        val completedAt = if (status == "taken") System.currentTimeMillis() else null
        reminderLogDao.updateLogStatus(uid, logId, status, completedAt)
    }

    suspend fun analyzeMedicinePackage(bitmap: Bitmap): AiResult<MedicineScanResult> {
        return visionService.analyzeMedicinePackage(bitmap)
    }

    suspend fun saveScanHistory(scan: ScanHistoryEntity): Long {
        val uid = activeUserId.value
        return scanHistoryDao.insertScanHistory(scan.copy(userId = uid))
    }

    suspend fun deleteScanHistory(id: Long) {
        val uid = activeUserId.value
        scanHistoryDao.deleteScanById(uid, id)
    }

    suspend fun clearScanHistory() {
        val uid = activeUserId.value
        scanHistoryDao.deleteAllScanHistoryForUser(uid)
    }

    suspend fun clearAllData() {
        val uid = activeUserId.value
        medicineDao.deleteAllMedicinesForUser(uid)
        reminderDao.deleteAllRemindersForUser(uid)
        reminderLogDao.deleteAllLogsForUser(uid)
        scanHistoryDao.deleteAllScanHistoryForUser(uid)
        knowledgeDao?.deleteAllKnowledge()
    }

    fun getMedicineKnowledgeFlow(medicineId: Long): Flow<MedicineKnowledgeEntity?> {
        return knowledgeDao?.getKnowledgeByMedicineIdFlow(medicineId) ?: kotlinx.coroutines.flow.flowOf(null)
    }

    suspend fun getOrFetchMedicineKnowledge(medicine: MedicineEntity): MedicineKnowledgeEntity? {
        return medicationKnowledgeRepo?.getOrFetchKnowledge(medicine)
    }

    suspend fun simplifyMedicineExplanation(medicineId: Long, currentExplanation: String): String? {
        return medicationKnowledgeRepo?.simplifyExplanation(medicineId, currentExplanation)
    }

    suspend fun askAiAssistant(prompt: String, history: List<Pair<String, String>> = emptyList()): AiResult<String> {
        return aiAssistantService.getHealthAssistantResponse(prompt, history)
    }

    companion object {
        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
