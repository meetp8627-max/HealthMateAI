package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ReminderLogEntity
import com.example.data.scheduler.ScheduleUtils
import com.example.ui.components.HealthProgressCard
import com.example.ui.components.InteractiveCalendar
import com.example.ui.components.ReminderItemCard
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassModal
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.viewmodel.HealthMateViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: HealthMateViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToScheduler: (Long, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val todayStr = remember { ScheduleUtils.getTodayDateString() }
    val todayCal = remember { Calendar.getInstance() }

    var selectedDateStr by remember { mutableStateOf(todayStr) }
    var displayedYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) }

    val activeReminders by viewModel.activeReminders.collectAsState()
    val dayOccurrences by remember(selectedDateStr) { viewModel.getOccurrencesForDate(selectedDateStr) }.collectAsState()

    // Delete confirmation dialog state
    var pendingDeleteLog by remember { mutableStateOf<ReminderLogEntity?>(null) }
    var deleteChoice by remember { mutableStateOf("occurrence") } // "occurrence" or "entire"

    val scheduledDatesSet = remember(activeReminders, displayedYear, displayedMonth) {
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

    val takenCount = dayOccurrences.count { it.status == "taken" }
    val remainingCount = dayOccurrences.count { it.status == "upcoming" }
    val missedCount = dayOccurrences.count { it.status == "missed" }

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Medication Reminders",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    actions = {
                        IconButton(onClick = { onNavigateToScheduler(0L, "") }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Reminder",
                                tint = PolishBluePrimary
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
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calendar Overview in Liquid Glass
                item {
                    InteractiveCalendar(
                        selectedDates = listOf(selectedDateStr),
                        onDateSelected = { clicked ->
                            selectedDateStr = clicked
                        },
                        displayedYear = displayedYear,
                        displayedMonth = displayedMonth,
                        onMonthChange = { y, m ->
                            displayedYear = y
                            displayedMonth = m
                        },
                        scheduledDates = scheduledDatesSet,
                        allowPastDates = true,
                        multiSelect = false
                    )
                }

                // Health Progress Card for Selected Date
                item {
                    HealthProgressCard(
                        takenCount = takenCount,
                        remainingCount = remainingCount,
                        missedCount = missedCount
                    )
                }

                // Scheduled Medicines Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedDateStr == todayStr) "Today's Schedule" else ScheduleUtils.formatDisplayDateShort(selectedDateStr),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                            Text(
                                text = ScheduleUtils.formatDisplayDateFull(selectedDateStr),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }

                        GlassButton(
                            text = "Add",
                            icon = Icons.Default.Add,
                            variant = GlassButtonVariant.PrimaryAccent,
                            height = 40.dp,
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            onClick = { onNavigateToScheduler(0L, "") }
                        )
                    }
                }

                if (dayOccurrences.isEmpty()) {
                    item {
                        GlassCard(
                            level = GlassLevel.Soft,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = if (isDark) PolishSlate500 else PolishSlate300
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No medicines scheduled for this day.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap 'Add' or scan a medicine package to create a schedule.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate400 else PolishSlate500
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                GlassButton(
                                    text = "Scan & Set Reminder",
                                    icon = Icons.Default.Add,
                                    variant = GlassButtonVariant.SolidDark,
                                    onClick = onNavigateToScan
                                )
                            }
                        }
                    }
                } else {
                    items(dayOccurrences) { log ->
                        Column {
                            ReminderItemCard(
                                log = log,
                                onMarkTaken = { viewModel.markOccurrenceStatus(log, "taken") },
                                onMarkSkipped = { viewModel.markOccurrenceStatus(log, "skipped") }
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, end = 8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = { onNavigateToScheduler(log.medicineId, log.medicineName) }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Schedule", fontSize = 12.sp, color = PolishBluePrimary)
                                }

                                TextButton(
                                    onClick = { pendingDeleteLog = log }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                item {
                    SafetyDisclaimerBanner()
                }
            }
        }
    }

    // Liquid Glass Delete Confirmation Modal
    if (pendingDeleteLog != null) {
        val log = pendingDeleteLog
        GlassModal(
            onDismissRequest = { pendingDeleteLog = null },
            title = "Delete Reminder",
            confirmButton = {
                GlassButton(
                    text = "Delete",
                    variant = GlassButtonVariant.Destructive,
                    height = 42.dp,
                    onClick = {
                        if (log != null) {
                            if (deleteChoice == "entire") {
                                viewModel.deleteReminder(log.reminderId, context)
                            } else {
                                viewModel.markOccurrenceStatus(log, "skipped")
                            }
                        }
                        pendingDeleteLog = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteLog = null }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Column {
                Text(
                    text = "Delete reminder for ${log?.medicineName}?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color.White else PolishSlate900
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = deleteChoice == "occurrence",
                        onClick = { deleteChoice = "occurrence" },
                        colors = RadioButtonDefaults.colors(selectedColor = PolishBluePrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete only this occurrence (${selectedDateStr})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = deleteChoice == "entire",
                        onClick = { deleteChoice = "entire" },
                        colors = RadioButtonDefaults.colors(selectedColor = PolishBluePrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete entire recurring schedule",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }
            }
        }
    }
}
