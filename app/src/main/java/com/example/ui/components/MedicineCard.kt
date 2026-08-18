package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MedicineEntity
import com.example.data.model.ReminderLogEntity
import com.example.data.scheduler.ReminderScheduler
import com.example.ui.animation.HealthMateMotion
import com.example.ui.animation.pressScale
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassPill
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishEmeraldBg
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate600
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.StatusAlert
import com.example.ui.viewmodel.HealthMateViewModel

@Composable
fun MedicineCard(
    medicine: MedicineEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasReminder: Boolean = true
) {
    val isDark = isSystemInDarkTheme()
    val isExpired = HealthMateViewModel.isExpired(medicine.expiryDate)

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDark) PolishBluePrimary.copy(alpha = 0.25f) else PolishBlueContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = medicine.name,
                    tint = PolishBluePrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = medicine.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    if (isExpired) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Expired",
                            tint = StatusAlert,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (medicine.brandName.isNotBlank()) {
                    Text(
                        text = "Brand: ${medicine.brandName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) PolishSlate400 else PolishSlate500
                    )
                }

                Text(
                    text = "${medicine.strength} • ${medicine.form}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PolishBluePrimary
                )

                if (medicine.expiryDate.isNotBlank() && medicine.expiryDate != "Not clearly visible") {
                    Text(
                        text = if (isExpired) "Expired: ${medicine.expiryDate}" else "Exp: ${medicine.expiryDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isExpired) StatusAlert else if (isDark) PolishSlate500 else PolishSlate400
                    )
                }
            }

            if (hasReminder) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isDark) PolishEmerald.copy(alpha = 0.2f) else PolishEmeraldBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Active Reminder",
                        tint = PolishEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReminderItemCard(
    log: ReminderLogEntity,
    onMarkTaken: () -> Unit,
    onMarkSkipped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isTaken = log.status == "taken"
    val isSkipped = log.status == "skipped"
    val isMissed = log.status == "missed"

    val cardTint = when {
        isTaken -> if (isDark) PolishEmerald.copy(alpha = 0.15f) else PolishEmeraldBg
        isMissed -> StatusAlert.copy(alpha = 0.08f)
        else -> null
    }

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(24.dp),
        tint = cardTint,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = log.scheduledTime,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PolishBluePrimary
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = log.medicineName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    if (log.dosageText.isNotBlank()) {
                        Text(
                            text = log.dosageText,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) PolishSlate400 else PolishSlate500
                        )
                    }
                }

                GlassPill(
                    text = when (log.status) {
                        "taken" -> "Taken ✓"
                        "skipped" -> "Skipped"
                        "missed" -> "Reminder Missed"
                        else -> "Upcoming"
                    },
                    icon = if (isTaken) Icons.Default.Check else if (isSkipped) Icons.Default.Close else null,
                    active = isTaken || isMissed,
                    accentColor = when {
                        isTaken -> PolishEmerald
                        isMissed -> StatusAlert
                        isSkipped -> PolishSlate500
                        else -> PolishBluePrimary
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(
                        onClick = {
                            val parts = log.scheduledTime.split(":")
                            val h = parts.getOrNull(0)?.toIntOrNull() ?: 8
                            val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                            ReminderScheduler.syncToSystemClockApp(
                                context = context,
                                medicineName = log.medicineName,
                                hour = h,
                                minute = m,
                                daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            )
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .pressScale(scaleDown = 0.90f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Sync System Clock Alarm",
                            tint = PolishBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            ReminderScheduler.triggerTestNotificationNow(context, log.medicineName)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .pressScale(scaleDown = 0.90f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Test Notification Alarm",
                            tint = if (isDark) PolishSlate400 else PolishSlate500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (!isTaken && !isSkipped) {
                    Row {
                        GlassButton(
                            text = "Skip",
                            variant = GlassButtonVariant.TranslucentGlass,
                            height = 38.dp,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp),
                            onClick = onMarkSkipped
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassButton(
                            text = "Mark Taken",
                            icon = Icons.Default.Check,
                            variant = GlassButtonVariant.PrimaryAccent,
                            height = 38.dp,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp),
                            onClick = onMarkTaken
                        )
                    }
                }
            }
        }
    }
}
