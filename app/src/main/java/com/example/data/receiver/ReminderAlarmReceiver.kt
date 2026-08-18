package com.example.data.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.scheduler.ReminderScheduler

class ReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return

        if (action == ACTION_MEDICINE_REMINDER) {
            val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0L)
            val medicineName = intent.getStringExtra(EXTRA_MEDICINE_NAME) ?: "Your Medicine"
            val timeStr = intent.getStringExtra(EXTRA_TIME_STR) ?: ""

            ReminderScheduler.ensureNotificationChannel(context)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("navigate_to", "reminders")
                putExtra("reminder_id", reminderId)
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                reminderId.toInt(),
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("💊 Medication Alarm: $medicineName")
                .setContentText("It's $timeStr - Time to take your prescribed dose of $medicineName.")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationId = (reminderId * 1000 + (System.currentTimeMillis() % 1000)).toInt()
            notificationManager.notify(notificationId, notification)
        } else if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-register alarms on device reboot if needed
            ReminderScheduler.ensureNotificationChannel(context)
        }
    }

    companion object {
        const val ACTION_MEDICINE_REMINDER = "com.example.ACTION_MEDICINE_REMINDER"
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
        const val EXTRA_TIME_STR = "extra_time_str"
    }
}
