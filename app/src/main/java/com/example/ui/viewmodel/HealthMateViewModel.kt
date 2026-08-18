package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiResult
import com.example.data.ai.MedicineScanResult
import com.example.data.model.MedicineEntity
import com.example.data.model.MedicineKnowledgeEntity
import com.example.data.model.ReminderEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.model.ScanHistoryEntity
import com.example.data.model.UserEntity
import com.example.data.repository.HealthMateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed interface ScanUiState {
    object Idle : ScanUiState
    data class Scanning(val message: String = "Scanning package...") : ScanUiState
    data class Verification(
        val bitmap: Bitmap?,
        val result: MedicineScanResult,
        val editedName: String,
        val editedBrand: String,
        val editedStrength: String,
        val editedForm: String,
        val editedManufacturer: String,
        val editedExpiry: String,
        val editedNotes: String,
        val duplicateMatch: MedicineEntity? = null
    ) : ScanUiState
    data class Saved(val medicineId: Long) : ScanUiState
    data class RateLimited(
        val retryAfterSeconds: Int,
        val capturedBitmap: Bitmap?
    ) : ScanUiState
    data class Error(
        val userFriendlyTitle: String,
        val userFriendlyMessage: String,
        val capturedBitmap: Bitmap?
    ) : ScanUiState
}

data class ChatMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HealthMateViewModel(private val repository: HealthMateRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.initUserSession()
        }
    }

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchQuery = MutableStateFlow("")
    val selectedFilter = MutableStateFlow("All") // All, Active, Expired, With Reminders, Without Reminders

    val allMedicines: StateFlow<List<MedicineEntity>> = repository.allMedicines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMedicines: StateFlow<List<MedicineEntity>> = combine(
        allMedicines,
        searchQuery,
        selectedFilter,
        repository.activeReminders
    ) { medicines, query, filter, reminders ->
        val reminderMedIds = reminders.map { it.medicineId }.toSet()
        medicines.filter { med ->
            val matchesQuery = query.isBlank() ||
                    med.name.contains(query, ignoreCase = true) ||
                    med.brandName.contains(query, ignoreCase = true) ||
                    med.activeIngredients.any { it.contains(query, ignoreCase = true) }

            val matchesFilter = when (filter) {
                "Active" -> med.verified
                "Expired" -> isExpired(med.expiryDate)
                "With Reminders" -> reminderMedIds.contains(med.id)
                "Without Reminders" -> !reminderMedIds.contains(med.id)
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeReminders: StateFlow<List<ReminderEntity>> = repository.activeReminders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayLogs: StateFlow<List<ReminderLogEntity>> = repository.getTodayLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<ReminderLogEntity>> = repository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val streakDays: StateFlow<Int> = allLogs.map { logs ->
        calculateDynamicStreak(logs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val scanHistory: StateFlow<List<ScanHistoryEntity>> = repository.scanHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "ai",
                text = "Hello! I am your HealthMate AI Assistant. I can help explain medication labels, terms like 'mg' vs 'mL', batch numbers, and reminder scheduling.\n\n⚠️ Disclaimer: I am an AI information assistant, not a doctor. I cannot diagnose, prescribe, or change your medications."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiAssistantLoading = MutableStateFlow(false)
    val aiAssistantLoading: StateFlow<Boolean> = _aiAssistantLoading.asStateFlow()

    fun switchUser(userId: String) {
        viewModelScope.launch {
            repository.switchUser(userId)
            _scanUiState.value = ScanUiState.Idle
        }
    }

    fun createOrSwitchUser(email: String, displayName: String) {
        viewModelScope.launch {
            repository.createOrSwitchUser(email, displayName)
            _scanUiState.value = ScanUiState.Idle
        }
    }

    fun updateUserProfile(newName: String, newEmail: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateUserProfile(user.id, newName, newEmail)
        }
    }

    fun saveUserName(name: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.updateUserProfile(user.id, name.trim(), user.email)
        }
    }

    fun resetPersonalization() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.resetPersonalization(user.id)
        }
    }

    private var aiRateLimitedUntilMs: Long = 0L

    fun analyzePackageImage(bitmap: Bitmap) {
        val now = System.currentTimeMillis()
        if (now < aiRateLimitedUntilMs) {
            val remainingSeconds = (((aiRateLimitedUntilMs - now) / 1000L).toInt()).coerceAtLeast(1)
            _scanUiState.value = ScanUiState.RateLimited(
                retryAfterSeconds = remainingSeconds,
                capturedBitmap = bitmap
            )
            return
        }

        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Scanning("Scanning package... Reading text...")
            when (val result = repository.analyzeMedicinePackage(bitmap)) {
                is AiResult.Success -> {
                    val scanResult = result.data
                    val existingMeds = repository.allMedicines.stateIn(viewModelScope).value
                    val duplicate = existingMeds.firstOrNull {
                        it.name.equals(scanResult.medicineName, ignoreCase = true) ||
                                (it.brandName.isNotEmpty() && it.brandName.equals(scanResult.brandName, ignoreCase = true))
                    }

                    _scanUiState.value = ScanUiState.Verification(
                        bitmap = bitmap,
                        result = scanResult,
                        editedName = if (scanResult.medicineName != "Not clearly visible") scanResult.medicineName else "",
                        editedBrand = if (scanResult.brandName != "Not clearly visible") scanResult.brandName else "",
                        editedStrength = if (scanResult.strength != "Not clearly visible") scanResult.strength else "",
                        editedForm = if (scanResult.dosageForm != "Not clearly visible") scanResult.dosageForm else "Tablet",
                        editedManufacturer = if (scanResult.manufacturer != "Not clearly visible") scanResult.manufacturer else "",
                        editedExpiry = if (scanResult.expiryDate != "Not clearly visible") scanResult.expiryDate else "",
                        editedNotes = if (scanResult.visibleWarnings.isNotEmpty()) "Warnings: ${scanResult.visibleWarnings.joinToString()}" else "",
                        duplicateMatch = duplicate
                    )

                    // Save scan history for current user
                    repository.saveScanHistory(
                        ScanHistoryEntity(
                            detectedName = scanResult.medicineName,
                            brandName = scanResult.brandName,
                            strength = scanResult.strength,
                            form = scanResult.dosageForm,
                            confidence = scanResult.confidence,
                            verified = false
                        )
                    )
                }

                is AiResult.RateLimited -> {
                    aiRateLimitedUntilMs = System.currentTimeMillis() + (result.retryAfterSeconds * 1000L)
                    _scanUiState.value = ScanUiState.RateLimited(
                        retryAfterSeconds = result.retryAfterSeconds,
                        capturedBitmap = bitmap
                    )
                }

                is AiResult.NetworkError -> {
                    _scanUiState.value = ScanUiState.Error(
                        userFriendlyTitle = "Connection Error",
                        userFriendlyMessage = "Couldn't connect to the AI service. Check your internet connection.",
                        capturedBitmap = bitmap
                    )
                }

                is AiResult.InvalidResponse -> {
                    _scanUiState.value = ScanUiState.Error(
                        userFriendlyTitle = "Unclear Package",
                        userFriendlyMessage = "We couldn't reliably read the medicine package. You can try again or enter details manually.",
                        capturedBitmap = bitmap
                    )
                }

                is AiResult.AuthError -> {
                    _scanUiState.value = ScanUiState.Error(
                        userFriendlyTitle = "AI Unavailable",
                        userFriendlyMessage = "AI scanning is currently unavailable.",
                        capturedBitmap = bitmap
                    )
                }

                is AiResult.UnknownError -> {
                    _scanUiState.value = ScanUiState.Error(
                        userFriendlyTitle = "Scan Unsuccessful",
                        userFriendlyMessage = result.userFriendlyMessage,
                        capturedBitmap = bitmap
                    )
                }
            }
        }
    }

    fun updateVerificationFields(
        name: String,
        brand: String,
        strength: String,
        form: String,
        manufacturer: String,
        expiry: String,
        notes: String
    ) {
        val current = _scanUiState.value
        if (current is ScanUiState.Verification) {
            _scanUiState.value = current.copy(
                editedName = name,
                editedBrand = brand,
                editedStrength = strength,
                editedForm = form,
                editedManufacturer = manufacturer,
                editedExpiry = expiry,
                editedNotes = notes
            )
        }
    }

    fun resetScanState() {
        _scanUiState.value = ScanUiState.Idle
    }

    fun saveVerifiedMedicine(onSaved: (Long) -> Unit) {
        val current = _scanUiState.value
        if (current is ScanUiState.Verification) {
            viewModelScope.launch {
                val newMedicine = MedicineEntity(
                    name = current.editedName.ifBlank { "Unidentified Medicine" },
                    brandName = current.editedBrand,
                    activeIngredients = current.result.activeIngredients,
                    strength = current.editedStrength,
                    form = current.editedForm,
                    manufacturer = current.editedManufacturer,
                    expiryDate = current.editedExpiry,
                    batchNumber = if (current.result.batchNumber != "Not clearly visible") current.result.batchNumber else "",
                    quantity = if (current.result.quantity != "Not clearly visible") current.result.quantity else "",
                    notes = current.editedNotes,
                    confidence = current.result.confidence,
                    verified = true
                )
                val id = repository.insertMedicine(newMedicine)
                val savedMed = newMedicine.copy(id = id)
                repository.getOrFetchMedicineKnowledge(savedMed)
                _scanUiState.value = ScanUiState.Saved(id)
                onSaved(id)
            }
        }
    }

    fun addMedicineManually(
        name: String,
        brand: String,
        strength: String,
        form: String,
        manufacturer: String,
        expiry: String,
        notes: String,
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val med = MedicineEntity(
                name = name,
                brandName = brand,
                strength = strength,
                form = form,
                manufacturer = manufacturer,
                expiryDate = expiry,
                notes = notes,
                confidence = "High",
                verified = true
            )
            val id = repository.insertMedicine(med)
            val savedMed = med.copy(id = id)
            repository.getOrFetchMedicineKnowledge(savedMed)
            onSaved(id)
        }
    }

    fun getMedicineKnowledgeFlow(medicineId: Long): Flow<MedicineKnowledgeEntity?> {
        return repository.getMedicineKnowledgeFlow(medicineId)
    }

    suspend fun getOrFetchMedicineKnowledge(medicine: MedicineEntity): MedicineKnowledgeEntity? {
        return repository.getOrFetchMedicineKnowledge(medicine)
    }

    private val _simplifyingMedicineId = MutableStateFlow<Long?>(null)
    val simplifyingMedicineId: StateFlow<Long?> = _simplifyingMedicineId.asStateFlow()

    fun simplifyMedicineExplanation(medicineId: Long, currentExplanation: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            _simplifyingMedicineId.value = medicineId
            val result = repository.simplifyMedicineExplanation(medicineId, currentExplanation)
            _simplifyingMedicineId.value = null
            onResult(result)
        }
    }

    fun getOccurrencesForDate(dateString: String): StateFlow<List<ReminderLogEntity>> {
        return repository.getOccurrencesForDate(dateString)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun markOccurrenceStatus(log: ReminderLogEntity, newStatus: String) {
        viewModelScope.launch {
            repository.markOccurrenceStatus(log, newStatus)
        }
    }

    fun checkDuplicateReminder(
        medicineId: Long,
        times: List<String>,
        scheduleType: String,
        startDate: String,
        onResult: (ReminderEntity?) -> Unit
    ) {
        viewModelScope.launch {
            val duplicate = repository.checkDuplicateReminder(medicineId, times, scheduleType, startDate)
            onResult(duplicate)
        }
    }

    fun saveReminderSchedule(
        context: android.content.Context,
        reminderId: Long = 0L,
        medicineId: Long,
        medicineName: String,
        scheduleType: String,
        startDate: String,
        endDate: String?,
        selectedDates: List<String>,
        weekdays: List<String>,
        dayOfMonth: Int?,
        times: List<String>,
        dosageText: String = "",
        notes: String = "",
        syncToSystemClock: Boolean = true,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val reminder = ReminderEntity(
                id = reminderId,
                medicineId = medicineId,
                medicineName = medicineName,
                scheduleType = scheduleType,
                startDate = startDate,
                endDate = endDate,
                selectedDates = selectedDates,
                weekdays = weekdays,
                dayOfMonth = dayOfMonth,
                times = times,
                dosageText = dosageText,
                notes = notes,
                enabled = true,
                updatedAt = System.currentTimeMillis()
            )

            val finalId = if (reminderId > 0) {
                repository.updateReminder(reminder)
                reminderId
            } else {
                repository.insertReminder(reminder)
            }

            val savedReminder = reminder.copy(id = finalId)
            com.example.data.scheduler.ReminderScheduler.scheduleReminder(
                context = context,
                reminder = savedReminder,
                syncToSystemClock = syncToSystemClock
            )
            onSaved()
        }
    }

    fun deleteReminder(reminderId: Long, context: android.content.Context? = null) {
        viewModelScope.launch {
            val reminder = repository.getReminderById(reminderId)
            if (reminder != null && context != null) {
                com.example.data.scheduler.ReminderScheduler.cancelReminder(context, reminder)
            }
            repository.deleteReminder(reminderId)
        }
    }

    fun createReminder(
        context: android.content.Context,
        medicineId: Long,
        medicineName: String,
        scheduleType: String,
        times: List<String>,
        days: List<String>,
        syncToSystemClock: Boolean = true,
        onCreated: () -> Unit
    ) {
        val todayStr = com.example.data.scheduler.ScheduleUtils.getTodayDateString()
        saveReminderSchedule(
            context = context,
            reminderId = 0L,
            medicineId = medicineId,
            medicineName = medicineName,
            scheduleType = if (scheduleType == "Once Daily") "daily" else scheduleType.lowercase(),
            startDate = todayStr,
            endDate = null,
            selectedDates = emptyList(),
            weekdays = days,
            dayOfMonth = null,
            times = times,
            syncToSystemClock = syncToSystemClock,
            onSaved = onCreated
        )
    }

    fun updateLogStatus(logId: Long, status: String) {
        viewModelScope.launch {
            repository.updateLogStatus(logId, status)
        }
    }

    fun deleteMedicine(id: Long) {
        viewModelScope.launch {
            repository.deleteMedicine(id)
        }
    }

    fun deleteScanHistory(id: Long) {
        viewModelScope.launch {
            repository.deleteScanHistory(id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _chatMessages.value = listOf(
                ChatMessage("ai", "Data cleared successfully. How can I assist you today?")
            )
            _scanUiState.value = ScanUiState.Idle
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val currentMsgs = _chatMessages.value.toMutableList()
        currentMsgs.add(ChatMessage("user", userText))
        _chatMessages.value = currentMsgs

        val now = System.currentTimeMillis()
        if (now < aiRateLimitedUntilMs) {
            val remaining = (((aiRateLimitedUntilMs - now) / 1000L).toInt()).coerceAtLeast(1)
            val updated = _chatMessages.value.toMutableList()
            updated.add(
                ChatMessage(
                    "ai",
                    "AI Assistant Temporarily Unavailable\n\nHealthMate AI is temporarily unavailable because the AI service has reached its current usage limit. Please try again in ${remaining}s."
                )
            )
            _chatMessages.value = updated
            return
        }

        _aiAssistantLoading.value = true

        viewModelScope.launch {
            val historyPairs = currentMsgs.map {
                (if (it.sender == "user") "user" else "model") to it.text
            }
            val result = repository.askAiAssistant(userText, historyPairs)
            _aiAssistantLoading.value = false

            val updated = _chatMessages.value.toMutableList()
            when (result) {
                is AiResult.Success -> {
                    updated.add(ChatMessage("ai", result.data))
                }
                is AiResult.RateLimited -> {
                    aiRateLimitedUntilMs = System.currentTimeMillis() + (result.retryAfterSeconds * 1000L)
                    updated.add(
                        ChatMessage(
                            "ai",
                            "AI Assistant Temporarily Unavailable\n\nHealthMate AI is temporarily unavailable because the AI service has reached its current usage limit."
                        )
                    )
                }
                is AiResult.NetworkError -> {
                    updated.add(
                        ChatMessage(
                            "ai",
                            "Couldn't connect to the AI service. Check your internet connection."
                        )
                    )
                }
                is AiResult.AuthError, is AiResult.InvalidResponse, is AiResult.UnknownError -> {
                    updated.add(
                        ChatMessage(
                            "ai",
                            "HealthMate AI is temporarily unavailable."
                        )
                    )
                }
            }
            _chatMessages.value = updated
        }
    }

    companion object {
        fun calculateDynamicStreak(logs: List<ReminderLogEntity>): Int {
            if (logs.isEmpty()) return 0
            val takenDates = logs.filter { it.status == "taken" }.map { it.dateString }.toSet()
            if (takenDates.isEmpty()) return 0

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            var streak = 0

            val todayStr = sdf.format(cal.time)
            if (takenDates.contains(todayStr)) {
                streak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterdayStr = sdf.format(cal.time)
                if (!takenDates.contains(yesterdayStr)) return 0
            }

            while (true) {
                val dateStr = sdf.format(cal.time)
                if (takenDates.contains(dateStr)) {
                    streak++
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                } else {
                    break
                }
            }
            return streak
        }

        fun isExpired(expiryString: String): Boolean {
            if (expiryString.isBlank() || expiryString.contains("Not clearly visible", ignoreCase = true)) return false
            return try {
                val parts = expiryString.split("/", "-")
                if (parts.size >= 2) {
                    val yearStr = parts.last()
                    val year = if (yearStr.length == 2) 2000 + yearStr.toInt() else yearStr.toInt()
                    val month = parts.first().toInt()
                    val currentYear = 2026
                    val currentMonth = 8
                    year < currentYear || (year == currentYear && month < currentMonth)
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }
}

class HealthMateViewModelFactory(private val repository: HealthMateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthMateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HealthMateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
