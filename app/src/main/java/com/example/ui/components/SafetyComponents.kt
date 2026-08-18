package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyAmberOnContainer
import com.example.ui.theme.StatusAlert

@Composable
fun SafetyDisclaimerBanner(
    modifier: Modifier = Modifier,
    isWarning: Boolean = false,
    text: String = "HealthMate AI provides informational assistance. Verify all data with your doctor. Never change dosage based on AI analysis."
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = if (isWarning) GlassLevel.Strong else GlassLevel.Soft,
        shape = RoundedCornerShape(20.dp),
        tint = if (isWarning) SafetyAmberContainer else null,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.Info,
                contentDescription = "Medical Disclaimer",
                modifier = Modifier.size(20.dp),
                tint = if (isWarning) SafetyAmberOnContainer else PolishBluePrimary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = if (isWarning) SafetyAmberOnContainer else if (isDark) PolishSlate400 else PolishSlate500
            )
        }
    }
}

@Composable
fun HealthProgressCard(
    takenCount: Int,
    remainingCount: Int,
    missedCount: Int,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val total = (takenCount + remainingCount + missedCount).coerceAtLeast(1)
    val progress = (takenCount.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val percentage = (progress * 100).toInt()

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(28.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Today's Medication Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$takenCount of ${takenCount + remainingCount} scheduled doses taken",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) PolishSlate400 else PolishSlate500
                    )
                }
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = PolishBluePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(PolishBluePrimary)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatBadge(label = "Taken", value = "$takenCount", color = PolishEmerald)
                Spacer(modifier = Modifier.width(16.dp))
                StatBadge(label = "Remaining", value = "$remainingCount", color = PolishBluePrimary)
                Spacer(modifier = Modifier.width(16.dp))
                StatBadge(
                    label = "Missed",
                    value = "$missedCount",
                    color = if (missedCount > 0) StatusAlert else if (isDark) PolishSlate500 else PolishSlate300
                )
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, color: Color) {
    val isDark = isSystemInDarkTheme()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = if (isDark) PolishSlate400 else PolishSlate500
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else PolishSlate900
        )
    }
}
