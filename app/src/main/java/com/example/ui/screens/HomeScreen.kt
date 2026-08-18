package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.animation.AnimatedNumberText
import com.example.ui.animation.HealthMateMotion
import com.example.ui.animation.pressScale
import com.example.ui.animation.staggeredEntrance
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassPill
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishEmeraldBg
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.viewmodel.HealthMateViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HealthMateViewModel,
    onNavigateToScan: () -> Unit,
    onNavigateToMedicines: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToHealthInsights: () -> Unit,
    onMedicineClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()

    val displayName = currentUser?.displayName ?: ""
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 0..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val greetingText = if (displayName.isNotBlank()) "$timeGreeting, $displayName" else timeGreeting

    val initials = if (displayName.isNotBlank()) {
        displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase()
    } else {
        "HM"
    }

    val takenCount = todayLogs.count { it.status == "taken" }
    val totalDoses = todayLogs.size
    val adherencePercent = if (totalDoses == 0) 100 else ((takenCount.toFloat() / totalDoses) * 100).toInt()

    val nextReminder = todayLogs.firstOrNull { it.status == "upcoming" }

    val dateFormatted = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    GlassAtmosphericBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Top Section: Header + Glass Contextual Elements
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(index = 0),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) PolishSlate400 else PolishSlate500
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // AI Mode Glass Pill
                        GlassPill(
                            text = "AI Mode",
                            icon = Icons.Default.Psychology,
                            active = true,
                            accentColor = PolishBluePrimary,
                            onClick = onNavigateToAiAssistant
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Streak Glass Pill
                        GlassPill(
                            text = "🔥 $streakDays d",
                            active = true,
                            accentColor = PolishEmerald
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer)
                                .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishBlueDark
                            )
                        }
                    }
                }
            }

            // 2. Today's Adherence Liquid Glass Card
            item {
                GlassCard(
                    level = GlassLevel.Soft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(index = 1),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TODAY'S ADHERENCE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) PolishSlate400 else PolishSlate400,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedNumberText(
                                    value = adherencePercent,
                                    suffix = "%",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PolishBluePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$takenCount of $totalDoses reminders completed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate400 else PolishSlate500,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Segmented Liquid Progress Bar
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val totalSegments = 5
                                val activeSegments = if (totalDoses == 0) 5 else ((takenCount.toFloat() / totalDoses) * totalSegments).toInt()
                                repeat(totalSegments) { index ->
                                    val active = (index + 1) <= activeSegments
                                    val segmentColor by animateColorAsState(
                                        targetValue = if (active) PolishBluePrimary else if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100,
                                        animationSpec = tween(durationMillis = HealthMateMotion.StandardDuration),
                                        label = "segmentColor"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(CircleShape)
                                            .background(segmentColor)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        TextButton(
                            onClick = onNavigateToReminders,
                            modifier = Modifier.pressScale(scaleDown = 0.94f),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Details →", fontWeight = FontWeight.Bold, color = PolishBluePrimary, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 2.5 AI Assistant Feature Card (Dark Liquid Glass Capsule)
            item {
                GlassCard(
                    level = GlassLevel.Ultra,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(index = 2),
                    shape = RoundedCornerShape(24.dp),
                    tint = PolishSlate900,
                    onClick = onNavigateToAiAssistant
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(PolishBluePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "HealthMate AI Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PolishBluePrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("AI CHAT", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = PolishBluePrimary)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ask questions about packaging terms, batch numbers & dosages",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open AI Mode",
                            tint = PolishBluePrimary
                        )
                    }
                }
            }

            // 3. NEXT UP Card (Liquid Glass Strong)
            item {
                GlassCard(
                    level = GlassLevel.Strong,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(index = 3),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    text = "NEXT UP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PolishBluePrimary,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nextReminder?.scheduledTime ?: "All Clear",
                                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }

                            GlassPill(
                                text = if (nextReminder != null) "💊 Upcoming" else "✓ Up to date",
                                active = nextReminder != null,
                                accentColor = if (nextReminder != null) PolishEmerald else PolishSlate500
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = nextReminder?.medicineName ?: "No pending doses for today",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                        Text(
                            text = nextReminder?.dosageText?.ifBlank { "Scheduled dose" } ?: "All scheduled doses completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) PolishSlate400 else PolishSlate500
                        )

                        if (nextReminder != null) {
                            Spacer(modifier = Modifier.height(16.dp))

                            GlassButton(
                                text = "Mark as Taken",
                                icon = Icons.Default.Check,
                                variant = GlassButtonVariant.PrimaryAccent,
                                onClick = { viewModel.updateLogStatus(nextReminder.id, "taken") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 4. Today's Medication Timeline
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    TextButton(onClick = onNavigateToReminders) {
                        Text("View All", fontSize = 13.sp, color = PolishBluePrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (todayLogs.isEmpty()) {
                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PolishBlueContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Medication, contentDescription = null, tint = PolishBluePrimary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Your medicine cabinet & schedule is empty",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                                Text(
                                    text = "Scan a medicine package to add your first reminder",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate400 else PolishSlate500
                                )
                            }
                        }
                    }
                }
            } else {
                items(todayLogs) { log ->
                    val isTaken = log.status == "taken"
                    val isSkipped = log.status == "skipped"

                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = log.scheduledTime,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isTaken) PolishSlate400 else PolishBluePrimary,
                                modifier = Modifier.width(68.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isTaken -> PolishEmeraldBg
                                            isSkipped -> if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100
                                            else -> PolishBlueContainer
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isTaken) {
                                    Icon(Icons.Default.Check, contentDescription = "Taken", tint = PolishEmerald, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = if (isSkipped) "—" else "○",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSkipped) PolishSlate400 else PolishBluePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.medicineName,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isTaken) PolishSlate400 else if (isDark) Color.White else PolishSlate900
                                )
                                Text(
                                    text = log.dosageText.ifBlank { "Scheduled dosage" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) PolishSlate400 else PolishSlate500
                                )
                            }

                            if (!isTaken && !isSkipped) {
                                GlassButton(
                                    text = "Take",
                                    variant = GlassButtonVariant.PrimaryAccent,
                                    height = 36.dp,
                                    contentPadding = PaddingValues(horizontal = 14.dp),
                                    onClick = { viewModel.updateLogStatus(log.id, "taken") }
                                )
                            } else if (isTaken) {
                                Text("Taken ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PolishEmerald)
                            }
                        }
                    }
                }
            }

            // 5. Health Insights Section (Glass Card)
            item {
                GlassCard(
                    level = GlassLevel.Soft,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToHealthInsights
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PolishBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = PolishBluePrimary)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Health Insights",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                            Text(
                                text = "7-day trend • Adherence • Consistency",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = PolishSlate400)
                    }
                }
            }

            // 6. Secondary Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionGlassTile(
                        title = "Scan Medicine",
                        subtitle = "Camera AI Reader",
                        icon = Icons.Default.CameraAlt,
                        iconTint = PolishBluePrimary,
                        onClick = onNavigateToScan,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionGlassTile(
                        title = "View Calendar",
                        subtitle = "Monthly Schedule",
                        icon = Icons.Default.CalendarMonth,
                        iconTint = PolishEmerald,
                        onClick = onNavigateToReminders,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Safety Disclaimer Banner
            item {
                SafetyDisclaimerBanner()
            }
        }
    }
}

@Composable
private fun QuickActionGlassTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }
    }
}
