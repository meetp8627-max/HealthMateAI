package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.HealthMateMotion
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.navigation.Screen
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate900

/**
 * Next-Generation Liquid Glass Floating Bottom Navigation Bar.
 * Floats gracefully above the content with Glass Ultra translucency,
 * animated active glass pill, and a tactile 3D glass SCAN trigger.
 */
@Composable
fun FloatingBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Glass Ultra Navigation Bar
        GlassCard(
            level = GlassLevel.Ultra,
            shape = RoundedCornerShape(36.dp),
            elevation = 12.dp,
            pressEffect = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left 2 items: Home, Calendar
                NavItemCell(
                    screen = Screen.Home,
                    selected = currentRoute == Screen.Home.route,
                    onClick = { onNavigate(Screen.Home) },
                    modifier = Modifier.weight(1f)
                )

                NavItemCell(
                    screen = Screen.Calendar,
                    selected = currentRoute == Screen.Calendar.route,
                    onClick = { onNavigate(Screen.Calendar) },
                    modifier = Modifier.weight(1f)
                )

                // Dedicated spacing for center floating 3D SCAN glass button
                Spacer(modifier = Modifier.weight(1.2f))

                // Right 2 items: Medicines, Profile
                NavItemCell(
                    screen = Screen.Medicines,
                    selected = currentRoute == Screen.Medicines.route,
                    onClick = { onNavigate(Screen.Medicines) },
                    modifier = Modifier.weight(1f)
                )

                NavItemCell(
                    screen = Screen.Settings,
                    selected = currentRoute == Screen.Settings.route,
                    onClick = { onNavigate(Screen.Settings) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Center Floating 3D Liquid Glass SCAN Button
        val scanInteractionSource = remember { MutableInteractionSource() }
        val isScanPressed by scanInteractionSource.collectIsPressedAsState()
        val scanScale by animateFloatAsState(
            targetValue = if (isScanPressed) 0.90f else 1.0f,
            animationSpec = HealthMateMotion.springTactile(),
            label = "ScanButtonScale"
        )

        val infiniteTransition = rememberInfiniteTransition(label = "ScanGlowPulse")
        val breathGlowScale by infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = HealthMateMotion.NaturalEaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ScanGlowScale"
        )
        val breathGlowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.30f,
            targetValue = 0.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = HealthMateMotion.NaturalEaseInOut),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ScanGlowAlpha"
        )

        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .scale(scanScale)
                .clickable(
                    interactionSource = scanInteractionSource,
                    indication = null,
                    onClick = { onNavigate(Screen.Scan) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Ambient breathing refraction ring
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(breathGlowScale)
                            .clip(CircleShape)
                            .background(PolishBluePrimary.copy(alpha = breathGlowAlpha))
                    )

                    // 3D Spherical Glass Primary SCAN Button
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = PolishBluePrimary.copy(alpha = 0.35f),
                                spotColor = PolishBluePrimary.copy(alpha = 0.45f)
                            )
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        PolishBluePrimary,
                                        PolishBlueDark
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.90f),
                                        Color.White.copy(alpha = 0.20f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Subtle specular top reflection lens inside glass sphere
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Scan Medicine",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "SCAN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else PolishBluePrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun NavItemCell(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.90f
            selected -> 1.10f
            else -> 1.0f
        },
        animationSpec = HealthMateMotion.springTactile(),
        label = "NavItemIconScale"
    )

    val pillAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = HealthMateMotion.StandardDuration),
        label = "NavItemPillAlpha"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            selected -> PolishBluePrimary
            isDark -> PolishSlate400
            else -> PolishSlate400
        },
        animationSpec = tween(durationMillis = HealthMateMotion.StandardDuration),
        label = "NavItemIconColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active Liquid Glass Pill Indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (pillAlpha > 0f) {
                        if (isDark) PolishBluePrimary.copy(alpha = 0.22f * pillAlpha)
                        else PolishBlueContainer.copy(alpha = 0.85f * pillAlpha)
                    } else Color.Transparent
                )
                .then(
                    if (pillAlpha > 0f) {
                        Modifier.border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.80f * pillAlpha),
                                    PolishBluePrimary.copy(alpha = 0.15f * pillAlpha)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            screen.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = screen.title,
                    tint = iconColor,
                    modifier = Modifier
                        .size(22.dp)
                        .scale(iconScale)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = screen.title,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = iconColor,
            maxLines = 1,
            letterSpacing = (-0.2).sp
        )
    }
}
