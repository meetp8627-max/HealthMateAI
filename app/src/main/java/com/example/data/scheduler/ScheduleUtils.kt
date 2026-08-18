package com.example.data.scheduler

import com.example.data.model.ReminderEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ScheduleUtils {

    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(Date())
    }

    fun parseDateString(dateStr: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = sdf.parse(dateStr) ?: return null
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun formatDateString(calendar: Calendar): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun formatDisplayDateShort(dateStr: String): String {
        val cal = parseDateString(dateStr) ?: return dateStr
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun formatDisplayDateFull(dateStr: String): String {
        val cal = parseDateString(dateStr) ?: return dateStr
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun formatDisplayDateMedium(dateStr: String): String {
        val cal = parseDateString(dateStr) ?: return dateStr
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun formatMonthYearHeader(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun formatDisplayTime(timeStr: String): String {
        return try {
            val parts = timeStr.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                val amPm = if (hour >= 12) "PM" else "AM"
                val hour12 = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, amPm)
            } else {
                timeStr
            }
        } catch (e: Exception) {
            timeStr
        }
    }

    fun getWeekdayName(dateStr: String): String {
        val cal = parseDateString(dateStr) ?: return ""
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> ""
        }
    }

    fun getDayOfMonth(dateStr: String): Int {
        val cal = parseDateString(dateStr) ?: return 1
        return cal.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Deterministically calculates whether a reminder schedule occurs on a target date (YYYY-MM-DD).
     */
    fun doesReminderOccurOnDate(reminder: ReminderEntity, targetDateStr: String): Boolean {
        if (!reminder.enabled) return false

        val targetCal = parseDateString(targetDateStr) ?: return false
        val targetMillis = targetCal.timeInMillis

        when (reminder.scheduleType.lowercase(Locale.getDefault())) {
            "once" -> {
                if (reminder.selectedDates.isNotEmpty()) {
                    return reminder.selectedDates.contains(targetDateStr)
                }
                return reminder.startDate == targetDateStr
            }
            "daily" -> {
                if (reminder.startDate.isNotEmpty()) {
                    val startCal = parseDateString(reminder.startDate) ?: return false
                    if (targetMillis < startCal.timeInMillis) return false
                }
                if (!reminder.endDate.isNullOrEmpty()) {
                    val endCal = parseDateString(reminder.endDate) ?: return true
                    if (targetMillis > endCal.timeInMillis) return false
                }
                return true
            }
            "weekly" -> {
                if (reminder.startDate.isNotEmpty()) {
                    val startCal = parseDateString(reminder.startDate) ?: return false
                    if (targetMillis < startCal.timeInMillis) return false
                }
                if (!reminder.endDate.isNullOrEmpty()) {
                    val endCal = parseDateString(reminder.endDate) ?: return true
                    if (targetMillis > endCal.timeInMillis) return false
                }
                val weekday = getWeekdayName(targetDateStr)
                return reminder.weekdays.contains(weekday)
            }
            "monthly" -> {
                if (reminder.startDate.isNotEmpty()) {
                    val startCal = parseDateString(reminder.startDate) ?: return false
                    if (targetMillis < startCal.timeInMillis) return false
                }
                if (!reminder.endDate.isNullOrEmpty()) {
                    val endCal = parseDateString(reminder.endDate) ?: return true
                    if (targetMillis > endCal.timeInMillis) return false
                }
                val targetDay = getDayOfMonth(targetDateStr)
                val expectedDay = reminder.dayOfMonth ?: getDayOfMonth(reminder.startDate.ifEmpty { targetDateStr })
                return targetDay == expectedDay
            }
            "custom" -> {
                return reminder.selectedDates.contains(targetDateStr)
            }
            else -> {
                return reminder.selectedDates.contains(targetDateStr) || reminder.startDate == targetDateStr
            }
        }
    }

    /**
     * Deterministically generates human-readable schedule summaries without AI.
     */
    fun generateScheduleSummary(
        scheduleType: String,
        startDate: String,
        endDate: String?,
        selectedDates: List<String>,
        weekdays: List<String>,
        dayOfMonth: Int?,
        times: List<String>
    ): String {
        val timesFormatted = times.map { formatDisplayTime(it) }.joinToString(", ")
        val timeSuffix = if (timesFormatted.isNotEmpty()) " at $timesFormatted" else ""

        return when (scheduleType.lowercase(Locale.getDefault())) {
            "once" -> {
                if (selectedDates.size > 1) {
                    "${selectedDates.size} dates selected ($timesFormatted)"
                } else {
                    val d = selectedDates.firstOrNull() ?: startDate
                    "${formatDisplayDateMedium(d)}$timeSuffix"
                }
            }
            "daily" -> {
                if (!endDate.isNullOrEmpty()) {
                    "${formatDisplayDateShort(startDate)}–${formatDisplayDateShort(endDate)}$timeSuffix"
                } else {
                    "Every day$timeSuffix"
                }
            }
            "weekly" -> {
                if (weekdays.isEmpty()) {
                    "Every week$timeSuffix"
                } else if (weekdays.size == 7) {
                    "Every day$timeSuffix"
                } else {
                    val fullDays = weekdays.map { day ->
                        when (day.uppercase()) {
                            "MON" -> "Monday"
                            "TUE" -> "Tuesday"
                            "WED" -> "Wednesday"
                            "THU" -> "Thursday"
                            "FRI" -> "Friday"
                            "SAT" -> "Saturday"
                            "SUN" -> "Sunday"
                            else -> day
                        }
                    }
                    val daysStr = if (fullDays.size > 1) {
                        fullDays.dropLast(1).joinToString(", ") + " and " + fullDays.last()
                    } else {
                        fullDays.firstOrNull() ?: "week"
                    }
                    "Every $daysStr$timeSuffix"
                }
            }
            "monthly" -> {
                val dayNum = dayOfMonth ?: getDayOfMonth(startDate.ifEmpty { getTodayDateString() })
                val suffix = when (dayNum) {
                    1, 21, 31 -> "st"
                    2, 22 -> "nd"
                    3, 23 -> "rd"
                    else -> "th"
                }
                "Every month on the $dayNum$suffix$timeSuffix"
            }
            "custom" -> {
                if (selectedDates.isEmpty()) {
                    "No dates selected"
                } else if (selectedDates.size == 1) {
                    "${formatDisplayDateMedium(selectedDates.first())}$timeSuffix"
                } else {
                    "${selectedDates.size} dates selected$timeSuffix"
                }
            }
            else -> "Scheduled$timeSuffix"
        }
    }
}
