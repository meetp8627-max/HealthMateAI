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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HealthProgressCard
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishEmeraldBg
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.viewmodel.HealthMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthDashboardScreen(
    viewModel: HealthMateViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()
    val activeReminders by viewModel.activeReminders.collectAsStateWithLifecycle()
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()

    val takenCount = todayLogs.count { it.status == "taken" }
    val remainingCount = todayLogs.count { it.status == "upcoming" }
    val missedCount = todayLogs.count { it.status == "missed" }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklyStatus = listOf(true, true, true, true, false, false, false)

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Health & Adherence",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
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
                item {
                    HealthProgressCard(
                        takenCount = takenCount,
                        remainingCount = remainingCount,
                        missedCount = missedCount
                    )
                }

                // Weekly Overview Calendar Card (Liquid Glass)
                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = PolishBluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Weekly Adherence Overview",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else PolishSlate900
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                daysOfWeek.forEachIndexed { idx, day ->
                                    val completed = weeklyStatus[idx]
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = day,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) PolishSlate400 else PolishSlate500
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (completed) PolishEmeraldBg
                                                    else if (isDark) Color.White.copy(alpha = 0.05f)
                                                    else PolishSlate100
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (completed) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Completed",
                                                    tint = PolishEmerald,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            } else {
                                                Text(
                                                    "○",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (isDark) PolishSlate400 else PolishSlate300
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Stat Summary Cards Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryMetricCard(
                            title = "Saved Medicines",
                            value = "${medicines.size}",
                            subtitle = "In your cabinet",
                            modifier = Modifier.weight(1f)
                        )
                        SummaryMetricCard(
                            title = "Active Reminders",
                            value = "${activeReminders.size}",
                            subtitle = "Schedules set",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    SafetyDisclaimerBanner(
                        text = "HealthMate AI provides adherence tracking for information and reminder convenience. It does not provide medical diagnoses or alter prescribed dosage schedules."
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = PolishBluePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }
    }
}
