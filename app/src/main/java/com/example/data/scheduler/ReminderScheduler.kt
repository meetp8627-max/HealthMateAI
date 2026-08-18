package com.example.data.scheduler

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.ReminderEntity
import com.example.data.receiver.ReminderAlarmReceiver
import java.util.ArrayList
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_ID = "medication_reminders_channel"
    const val CHANNEL_NAME = "Medication Alarms & Reminders"

    /**
     * Schedules a reminder both locally in AlarmManager (for app notifications)
     * and optionally creates a System Clock Alarm in the device's Clock app.
     */
    fun scheduleReminder(
        context: Context,
        reminder: ReminderEntity,
        syncToSystemClock: Boolean = true
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        // 1. Schedule local AlarmManager notifications for each dose time
        reminder.times.forEachIndexed { index, timeStr ->
            try {
                val parts = timeStr.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toIntOrNull() ?: 8
                    val minute = parts[1].toIntOrNull() ?: 0

                    val pendingIntent = getPendingIntent(context, reminder, index, timeStr)

                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                        // If time has already passed today, set for tomorrow
                        if (timeInMillis <= System.currentTimeMillis()) {
                            add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }

                    if (alarmManager != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                                alarmManager.setAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.timeInMillis,
                                    pendingIntent
                                )
                            } else {
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    calendar.timeInMillis,
                                    pendingIntent
                                )
                            }
                        } else {
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                calendar.timeInMillis,
                                pendingIntent
                            )
                        }
                    }

                    // 2. Sync to System Clock App if requested
                    if (syncToSystemClock) {
                        syncToSystemClockApp(
                            context = context,
                            medicineName = reminder.medicineName,
                            hour = hour,
                            minute = minute,
                            daysOfWeek = reminder.weekdays
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Syncs a specific dose time directly to the Android System Clock Alarm App.
     */
    fun syncToSystemClockApp(
        context: Context,
        medicineName: String,
        hour: Int,
        minute: Int,
        daysOfWeek: List<String>
    ) {
        try {
            val daysList = ArrayList<Int>()
            daysOfWeek.forEach { day ->
                when (day.take(3).uppercase()) {
                    "MON" -> daysList.add(Calendar.MONDAY)
                    "TUE" -> daysList.add(Calendar.TUESDAY)
                    "WED" -> daysList.add(Calendar.WEDNESDAY)
                    "THU" -> daysList.add(Calendar.THURSDAY)
                    "FRI" -> daysList.add(Calendar.FRIDAY)
                    "SAT" -> daysList.add(Calendar.SATURDAY)
                    "SUN" -> daysList.add(Calendar.SUNDAY)
                }
            }

            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_MESSAGE, "💊 Take $medicineName")
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                putExtra(AlarmClock.EXTRA_VIBRATE, true)
                if (daysList.isNotEmpty()) {
                    putExtra(AlarmClock.EXTRA_DAYS, daysList)
                }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            Toast.makeText(
                context,
                "Synced System Clock Alarm for $medicineName at ${String.format("%02d:%02d", hour, minute)}",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: ActivityNotFoundException) {
            // Fallback if system clock doesn't support silent alarm setting
            try {
                val fallbackIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, "💊 Take $medicineName")
                    putExtra(AlarmClock.EXTRA_HOUR, hour)
                    putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not launch System Clock app", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission denied to set system alarm", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cancels scheduled alarms in AlarmManager for a reminder.
     */
    fun cancelReminder(context: Context, reminder: ReminderEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        reminder.times.forEachIndexed { index, timeStr ->
            val pendingIntent = getPendingIntent(context, reminder, index, timeStr)
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Instantly triggers a test system notification so the user can test alarm sounds/notifications.
     */
    fun triggerTestNotificationNow(context: Context, medicineName: String) {
        ensureNotificationChannel(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            9999,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("💊 Medication Alarm: $medicineName")
            .setContentText("It's time to take your dose of $medicineName. Tap to record dosage.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        Toast.makeText(context, "Test notification alarm sent!", Toast.LENGTH_SHORT).show()
    }

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alarms and reminders for medication dosage schedules"
                    enableVibration(true)
                    enableLights(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun getPendingIntent(
        context: Context,
        reminder: ReminderEntity,
        index: Int,
        timeStr: String
    ): PendingIntent {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ReminderAlarmReceiver.ACTION_MEDICINE_REMINDER
            putExtra(ReminderAlarmReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderAlarmReceiver.EXTRA_MEDICINE_NAME, reminder.medicineName)
            putExtra(ReminderAlarmReceiver.EXTRA_TIME_STR, timeStr)
        }
        val requestCode = (reminder.id * 100 + index).toInt()
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
