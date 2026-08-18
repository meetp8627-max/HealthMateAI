package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.scheduler.ScheduleUtils
import com.example.ui.animation.HealthMateMotion
import com.example.ui.animation.pressScale
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassPill
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import java.util.Calendar

data class CalendarDay(
    val dateStr: String, // YYYY-MM-DD
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isPast: Boolean,
    val hasReminders: Boolean = false
)

@Composable
fun InteractiveCalendar(
    selectedDates: List<String>,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    multiSelect: Boolean = true,
    scheduledDates: Set<String> = emptySet(),
    allowPastDates: Boolean = false,
    displayedYear: Int,
    displayedMonth: Int, // 0-indexed Calendar.MONTH (0..11)
    onMonthChange: (Int, Int) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val todayStr = remember { ScheduleUtils.getTodayDateString() }
    val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    val calendarDays = remember(displayedYear, displayedMonth, scheduledDates, allowPastDates) {
        val days = mutableListOf<CalendarDay>()
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear)
            set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 for SUN
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Previous month filler
        val prevCal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
        val prevDaysInMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in (prevDaysInMonth - firstDayOfWeek + 1)..prevDaysInMonth) {
            val dateStr = String.format("%04d-%02d-%02d", prevCal.get(Calendar.YEAR), prevCal.get(Calendar.MONTH) + 1, i)
            days.add(
                CalendarDay(
                    dateStr = dateStr,
                    dayOfMonth = i,
                    isCurrentMonth = false,
                    isToday = dateStr == todayStr,
                    isPast = dateStr < todayStr,
                    hasReminders = scheduledDates.contains(dateStr)
                )
            )
        }

        // Current month
        for (i in 1..daysInMonth) {
            val dateStr = String.format("%04d-%02d-%02d", displayedYear, displayedMonth + 1, i)
            days.add(
                CalendarDay(
                    dateStr = dateStr,
                    dayOfMonth = i,
                    isCurrentMonth = true,
                    isToday = dateStr == todayStr,
                    isPast = dateStr < todayStr,
                    hasReminders = scheduledDates.contains(dateStr)
                )
            )
        }

        // Next month filler
        val totalSoFar = days.size
        val remainingCells = (42 - totalSoFar) % 7
        val nextCal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        for (i in 1..remainingCells) {
            val dateStr = String.format("%04d-%02d-%02d", nextCal.get(Calendar.YEAR), nextCal.get(Calendar.MONTH) + 1, i)
            days.add(
                CalendarDay(
                    dateStr = dateStr,
                    dayOfMonth = i,
                    isCurrentMonth = false,
                    isToday = dateStr == todayStr,
                    isPast = dateStr < todayStr,
                    hasReminders = scheduledDates.contains(dateStr)
                )
            )
        }

        days
    }

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Calendar Header: Month Navigation & Today Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val newCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, displayedYear)
                                set(Calendar.MONTH, displayedMonth)
                                add(Calendar.MONTH, -1)
                            }
                            onMonthChange(newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH))
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = if (isDark) Color.White else PolishSlate900
                        )
                    }

                    Text(
                        text = ScheduleUtils.formatMonthYearHeader(displayedYear, displayedMonth),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = {
                            val newCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, displayedYear)
                                set(Calendar.MONTH, displayedMonth)
                                add(Calendar.MONTH, 1)
                            }
                            onMonthChange(newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH))
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = if (isDark) Color.White else PolishSlate900
                        )
                    }
                }

                GlassPill(
                    text = "Today",
                    icon = Icons.Default.Today,
                    active = false,
                    accentColor = PolishBluePrimary,
                    onClick = {
                        val todayCal = Calendar.getInstance()
                        onMonthChange(todayCal.get(Calendar.YEAR), todayCal.get(Calendar.MONTH))
                        onDateSelected(todayStr)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday Header Labels (SUN MON TUE WED THU FRI SAT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) PolishSlate400 else PolishSlate400,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Calendar Grid Cells
            AnimatedContent(
                targetState = Pair(displayedYear, displayedMonth),
                transitionSpec = {
                    val initialVal = initialState.first * 12 + initialState.second
                    val targetVal = targetState.first * 12 + targetState.second
                    val isForward = targetVal >= initialVal

                    if (isForward) {
                        (slideInHorizontally(
                            initialOffsetX = { width -> (width * 0.4f).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeIn(animationSpec = tween(200)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { width -> (-width * 0.4f).toInt() },
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                                ) + fadeOut(animationSpec = tween(150))
                            )
                    } else {
                        (slideInHorizontally(
                            initialOffsetX = { width -> (-width * 0.4f).toInt() },
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                        ) + fadeIn(animationSpec = tween(200)))
                            .togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { width -> (width * 0.4f).toInt() },
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)
                                ) + fadeOut(animationSpec = tween(150))
                            )
                    }
                },
                label = "CalendarMonthTransition"
            ) { _ ->
                Column {
                    calendarDays.chunked(7).forEach { weekRow ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekRow.forEach { day ->
                                val isSelected = selectedDates.contains(day.dateStr)
                                val isDisabled = !allowPastDates && day.isPast && !day.isToday

                                val cellBg by animateColorAsState(
                                    targetValue = when {
                                        isSelected -> PolishBluePrimary
                                        day.isToday -> if (isDark) PolishBluePrimary.copy(alpha = 0.25f) else PolishBlueContainer
                                        else -> Color.Transparent
                                    },
                                    animationSpec = tween(durationMillis = HealthMateMotion.FastDuration),
                                    label = "CellBg"
                                )

                                val textColor by animateColorAsState(
                                    targetValue = when {
                                        isSelected -> Color.White
                                        isDisabled -> if (isDark) PolishSlate500 else PolishSlate300
                                        !day.isCurrentMonth -> if (isDark) PolishSlate500 else PolishSlate400
                                        day.isToday -> if (isDark) Color.White else PolishBlueDark
                                        else -> if (isDark) Color.White else PolishSlate900
                                    },
                                    animationSpec = tween(durationMillis = HealthMateMotion.FastDuration),
                                    label = "CellTextColor"
                                )

                                val accessibleLabel = buildString {
                                    append(ScheduleUtils.formatDisplayDateFull(day.dateStr))
                                    if (isSelected) append(", selected")
                                    if (day.hasReminders) append(", has scheduled reminders")
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(cellBg)
                                        .then(
                                            if (isSelected) {
                                                Modifier
                                                    .shadow(4.dp, CircleShape, spotColor = PolishBluePrimary.copy(alpha = 0.4f))
                                                    .border(
                                                        1.dp,
                                                        Brush.verticalGradient(
                                                            listOf(
                                                                Color.White.copy(alpha = 0.8f),
                                                                Color.White.copy(alpha = 0.2f)
                                                            )
                                                        ),
                                                        CircleShape
                                                    )
                                            } else if (day.isToday) {
                                                Modifier.border(1.dp, PolishBluePrimary, CircleShape)
                                            } else Modifier
                                        )
                                        .pressScale(
                                            scaleDown = 0.88f,
                                            enabled = !isDisabled,
                                            onClick = { onDateSelected(day.dateStr) }
                                        )
                                        .semantics {
                                            contentDescription = accessibleLabel
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = day.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Medium,
                                            color = textColor,
                                            fontSize = 14.sp
                                        )

                                        if (day.hasReminders) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isSelected) Color.White else PolishBluePrimary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
