package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderEntity
import com.example.data.scheduler.ScheduleUtils
import com.example.ui.components.InteractiveCalendar
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassChip
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassModal
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate700
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyAmberOnContainer
import com.example.ui.viewmodel.HealthMateViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateReminderScreen(
    medicineId: Long,
    medicineName: String,
    viewModel: HealthMateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val allMedicines by viewModel.allMedicines.collectAsState()
    val activeReminders by viewModel.activeReminders.collectAsState()

    // Context & Selected Medicine
    var selectedMedId by remember { mutableStateOf(medicineId) }
    var selectedMedName by remember { mutableStateOf(medicineName) }
    var selectedMedObj by remember { mutableStateOf<MedicineEntity?>(null) }
    var showMedDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(medicineId, allMedicines) {
        if (selectedMedId > 0) {
            val med = allMedicines.find { it.id == selectedMedId }
            if (med != null) {
                selectedMedObj = med
                selectedMedName = med.name
            }
        } else if (allMedicines.isNotEmpty()) {
            val firstMed = allMedicines.first()
            selectedMedId = firstMed.id
            selectedMedName = firstMed.name
            selectedMedObj = firstMed
        }
    }

    // Date & Calendar State
    val todayStr = remember { ScheduleUtils.getTodayDateString() }
    val todayCal = remember { Calendar.getInstance() }

    var displayedYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) }

    var scheduleType by remember { mutableStateOf("daily") }
    var startDate by remember { mutableStateOf(todayStr) }
    var endDate by remember { mutableStateOf<String?>(null) }
    val selectedDates = remember { mutableStateListOf(todayStr) }

    // Weekdays & Day of Month
    val weekdays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun") }
    var dayOfMonth by remember { mutableIntStateOf(ScheduleUtils.getDayOfMonth(todayStr)) }

    // Times State
    val timesList = remember { mutableStateListOf("08:00") }

    // Settings
    var syncToSystemClock by remember { mutableStateOf(true) }

    // Duplicate Check State
    var duplicateReminderWarning by remember { mutableStateOf<ReminderEntity?>(null) }

    val scheduledDatesSet = remember(activeReminders) {
        val dates = mutableSetOf<String>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.MONTH, displayedMonth)
        cal.set(Calendar.YEAR, displayedYear)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..maxDays) {
            val dStr = String.format("%04d-%02d-%02d", displayedYear, displayedMonth + 1, day)
            if (activeReminders.any { ScheduleUtils.doesReminderOccurOnDate(it, dStr) }) {
                dates.add(dStr)
            }
        }
        dates
    }

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Medication Scheduler",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) Color.White else PolishSlate900
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Medicine Context Header (Ultra Glass)
                item {
                    GlassCard(
                        level = GlassLevel.Ultra,
                        tint = PolishBlueContainer,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "MEDICATION CONTEXT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishBlueDark
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showMedDropdown = true }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = null,
                                        tint = PolishBluePrimary,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "💊 ${selectedMedName.ifEmpty { "Select Medicine" }}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else PolishSlate900
                                        )
                                        Text(
                                            text = selectedMedObj?.let { "${it.strength} ${it.form}".trim() } ?: "Tap to change medicine",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDark) PolishSlate400 else PolishSlate500
                                        )
                                    }
                                }

                                if (allMedicines.size > 1) {
                                    GlassButton(
                                        text = "Change",
                                        variant = GlassButtonVariant.TranslucentGlass,
                                        height = 36.dp,
                                        onClick = { showMedDropdown = true }
                                    )

                                    DropdownMenu(
                                        expanded = showMedDropdown,
                                        onDismissRequest = { showMedDropdown = false }
                                    ) {
                                        allMedicines.forEach { med ->
                                            DropdownMenuItem(
                                                text = { Text("💊 ${med.name} (${med.strength})") },
                                                onClick = {
                                                    selectedMedId = med.id
                                                    selectedMedName = med.name
                                                    selectedMedObj = med
                                                    showMedDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "When should we remind you?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }
                    }
                }

                // 2. Interactive Calendar Component
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = PolishBluePrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = "Select Calendar Dates",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }

                            if (scheduleType == "custom" || scheduleType == "once") {
                                TextButton(
                                    onClick = { selectedDates.clear() }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Selection", fontSize = 12.sp, color = PolishBluePrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        InteractiveCalendar(
                            selectedDates = selectedDates.toList(),
                            onDateSelected = { clickedDate ->
                                if (scheduleType == "custom" || scheduleType == "once") {
                                    if (selectedDates.contains(clickedDate)) {
                                        if (selectedDates.size > 1) selectedDates.remove(clickedDate)
                                    } else {
                                        if (scheduleType == "once" && selectedDates.size >= 1) {
                                            selectedDates.clear()
                                        }
                                        selectedDates.add(clickedDate)
                                    }
                                    startDate = selectedDates.firstOrNull() ?: todayStr
                                } else {
                                    startDate = clickedDate
                                    if (!selectedDates.contains(clickedDate)) {
                                        selectedDates.clear()
                                        selectedDates.add(clickedDate)
                                    }
                                }
                            },
                            displayedYear = displayedYear,
                            displayedMonth = displayedMonth,
                            onMonthChange = { y, m ->
                                displayedYear = y
                                displayedMonth = m
                            },
                            scheduledDates = scheduledDatesSet
                        )
                    }
                }

                // 3. Date Selection Modes (Repeat Options)
                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Repeat Schedule Mode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val modes = listOf(
                                "once" to "Once",
                                "daily" to "Every Day",
                                "weekly" to "Every Week",
                                "monthly" to "Every Month",
                                "custom" to "Custom Dates"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                modes.forEach { (key, label) ->
                                    val isSelected = scheduleType == key
                                    GlassChip(
                                        text = label,
                                        selected = isSelected,
                                        onClick = {
                                            scheduleType = key
                                            if (key == "once" && selectedDates.size > 1) {
                                                val first = selectedDates.firstOrNull() ?: todayStr
                                                selectedDates.clear()
                                                selectedDates.add(first)
                                            }
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Mode Specific Parameters
                            when (scheduleType) {
                                "daily" -> {
                                    Text(
                                        text = "Active Range:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else PolishSlate900
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        GlassInput(
                                            value = startDate,
                                            onValueChange = { startDate = it },
                                            placeholder = "Start Date",
                                            label = "Start Date",
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                        GlassInput(
                                            value = endDate ?: "",
                                            onValueChange = { endDate = it.ifBlank { null } },
                                            placeholder = "Ongoing",
                                            label = "End Date (Optional)",
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }
                                }

                                "weekly" -> {
                                    Text(
                                        text = "Select Weekdays:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else PolishSlate900
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    val allWeekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        allWeekdays.forEach { day ->
                                            val isSel = weekdays.contains(day)
                                            GlassChip(
                                                text = day,
                                                selected = isSel,
                                                onClick = {
                                                    if (isSel) {
                                                        if (weekdays.size > 1) weekdays.remove(day)
                                                    } else {
                                                        weekdays.add(day)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }

                                "monthly" -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Day of Month:",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else PolishSlate900
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        GlassInput(
                                            value = dayOfMonth.toString(),
                                            onValueChange = {
                                                val num = it.toIntOrNull()
                                                if (num != null && num in 1..31) dayOfMonth = num
                                            },
                                            modifier = Modifier.width(90.dp),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "th of every month",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDark) PolishSlate300 else PolishSlate700
                                        )
                                    }
                                }

                                "custom" -> {
                                    Text(
                                        text = "Tap individual dates directly on the calendar grid above to select or unselect.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Time Selection & Presets
                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = PolishBluePrimary,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "Reminder Times",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else PolishSlate900
                                    )
                                }

                                TextButton(
                                    onClick = { timesList.add("12:00") }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Add Another Time", fontSize = 12.sp, color = PolishBluePrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Presets
                            Text(
                                text = "Quick Presets:",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val presets = listOf(
                                "Morning" to "08:00",
                                "Afternoon" to "14:00",
                                "Evening" to "18:00",
                                "Night" to "22:00"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presets.forEach { (label, timeVal) ->
                                    GlassChip(
                                        text = "$label (${ScheduleUtils.formatDisplayTime(timeVal)})",
                                        selected = false,
                                        onClick = {
                                            if (!timesList.contains(timeVal)) {
                                                timesList.add(timeVal)
                                            }
                                        }
                                    )
                                }
                            }

                            Text(
                                text = "⚠️ Presets set convenient time values only; they must match your doctor's instructions.",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDark) PolishSlate400 else PolishSlate400,
                                modifier = Modifier.padding(top = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Configured Times List
                            timesList.forEachIndexed { index, timeVal ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Dose #${index + 1}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else PolishSlate900
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))

                                    GlassInput(
                                        value = timeVal,
                                        onValueChange = { timesList[index] = it },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        trailingIcon = {
                                            Text(
                                                ScheduleUtils.formatDisplayTime(timeVal),
                                                fontSize = 11.sp,
                                                color = PolishBluePrimary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        }
                                    )

                                    if (timesList.size > 1) {
                                        IconButton(
                                            onClick = { timesList.removeAt(index) }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove Time", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Smart Schedule Summary Card
                item {
                    val summaryText = remember(scheduleType, startDate, endDate, selectedDates.size, weekdays.size, dayOfMonth, timesList.size) {
                        ScheduleUtils.generateScheduleSummary(
                            scheduleType = scheduleType,
                            startDate = startDate,
                            endDate = endDate,
                            selectedDates = selectedDates.toList(),
                            weekdays = weekdays.toList(),
                            dayOfMonth = dayOfMonth,
                            times = timesList.toList()
                        )
                    }

                    GlassCard(
                        level = GlassLevel.Strong,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "LIVE SCHEDULE SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PolishBluePrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = PolishBluePrimary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = summaryText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }

                            if (scheduleType == "custom" || scheduleType == "once") {
                                Text(
                                    text = "📅 ${selectedDates.size} dates selected: " + selectedDates.take(4).joinToString { ScheduleUtils.formatDisplayDateShort(it) } + if (selectedDates.size > 4) "..." else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate300 else PolishSlate500,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 6. System Clock Alarm Sync Switch
                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Alarm,
                                        contentDescription = null,
                                        tint = PolishBluePrimary,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Sync with System Clock App",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else PolishSlate900
                                        )
                                        Text(
                                            text = "Creates alarms in your device's System Clock app",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDark) PolishSlate400 else PolishSlate500
                                        )
                                    }
                                }
                                Switch(
                                    checked = syncToSystemClock,
                                    onCheckedChange = { syncToSystemClock = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = PolishBluePrimary
                                    )
                                )
                            }
                        }
                    }
                }

                item {
                    SafetyDisclaimerBanner()
                }

                // Save Reminder Action Button
                item {
                    GlassButton(
                        text = "Save Medication Schedule",
                        icon = Icons.Default.Check,
                        variant = GlassButtonVariant.PrimaryAccent,
                        height = 54.dp,
                        onClick = {
                            viewModel.checkDuplicateReminder(
                                medicineId = selectedMedId,
                                times = timesList.toList(),
                                scheduleType = scheduleType,
                                startDate = startDate
                            ) { existing ->
                                if (existing != null) {
                                    duplicateReminderWarning = existing
                                } else {
                                    viewModel.saveReminderSchedule(
                                        context = context,
                                        medicineId = selectedMedId,
                                        medicineName = selectedMedName,
                                        scheduleType = scheduleType,
                                        startDate = startDate,
                                        endDate = endDate,
                                        selectedDates = selectedDates.toList(),
                                        weekdays = weekdays.toList(),
                                        dayOfMonth = dayOfMonth,
                                        times = timesList.toList(),
                                        dosageText = selectedMedObj?.strength ?: "",
                                        syncToSystemClock = syncToSystemClock,
                                        onSaved = onNavigateBack
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // Duplicate Reminder Modal
    if (duplicateReminderWarning != null) {
        GlassModal(
            onDismissRequest = { duplicateReminderWarning = null },
            title = "Duplicate Schedule Warning",
            icon = Icons.Default.Schedule,
            iconTint = SafetyAmberOnContainer,
            confirmButton = {
                GlassButton(
                    text = "Create Anyway",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        duplicateReminderWarning = null
                        viewModel.saveReminderSchedule(
                            context = context,
                            medicineId = selectedMedId,
                            medicineName = selectedMedName,
                            scheduleType = scheduleType,
                            startDate = startDate,
                            endDate = endDate,
                            selectedDates = selectedDates.toList(),
                            weekdays = weekdays.toList(),
                            dayOfMonth = dayOfMonth,
                            times = timesList.toList(),
                            dosageText = selectedMedObj?.strength ?: "",
                            syncToSystemClock = syncToSystemClock,
                            onSaved = onNavigateBack
                        )
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        duplicateReminderWarning = null
                        onNavigateBack()
                    }
                ) {
                    Text("View Existing", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Text(
                "This reminder schedule already exists for $selectedMedName at ${timesList.joinToString()}. Would you like to view the existing reminder or create it anyway?",
                color = if (isDark) PolishSlate300 else PolishSlate700,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
