package com.example.ui.components.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.HealthMateMotion
import com.example.ui.animation.pressScale
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.SafetyAmberTertiary
import com.example.ui.theme.StatusAlert

enum class GlassPillVariant {
    Primary,
    Emerald,
    Amber,
    Rose,
    Slate,
    Neutral
}

/**
 * Sleek translucent Glass Pill for active tabs, status indicators, and badges.
 */
@Composable
fun GlassPill(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    active: Boolean = false,
    variant: GlassPillVariant? = null,
    accentColor: Color = when (variant) {
        GlassPillVariant.Primary -> PolishBluePrimary
        GlassPillVariant.Emerald -> PolishEmerald
        GlassPillVariant.Amber -> SafetyAmberTertiary
        GlassPillVariant.Rose -> StatusAlert
        GlassPillVariant.Slate -> PolishSlate400
        GlassPillVariant.Neutral -> PolishSlate400
        null -> PolishBluePrimary
    },
    shape: Shape = CircleShape,
    onClick: (() -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()

    val isActive = active || (variant != null)

    val bgColor by animateColorAsState(
        targetValue = when {
            isActive && isDark -> accentColor.copy(alpha = 0.28f)
            isActive && !isDark -> accentColor.copy(alpha = 0.14f)
            isDark -> Color.White.copy(alpha = 0.06f)
            else -> Color.White.copy(alpha = 0.70f)
        },
        animationSpec = tween(HealthMateMotion.StandardDuration),
        label = "pillBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isActive -> accentColor
            isDark -> Color.White.copy(alpha = 0.85f)
            else -> PolishSlate900
        },
        animationSpec = tween(HealthMateMotion.StandardDuration),
        label = "pillContentColor"
    )

    val borderBrush = if (isActive) {
        Brush.verticalGradient(
            colors = listOf(
                accentColor.copy(alpha = 0.60f),
                accentColor.copy(alpha = 0.20f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.80f),
                if (isDark) Color.White.copy(alpha = 0.04f) else accentColor.copy(alpha = 0.08f)
            )
        )
    }

    val clickModifier = if (onClick != null) {
        Modifier.pressScale(scaleDown = 0.94f, onClick = onClick)
    } else Modifier

    Box(
        modifier = modifier
            .then(clickModifier)
            .clip(shape)
            .background(bgColor)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Filter / action chip in Liquid Glass style.
 * Supports both label and text parameters.
 */
@Composable
fun GlassChip(
    label: String = "",
    text: String = label,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    activeColor: Color = PolishBluePrimary
) {
    val displayLabel = if (label.isNotEmpty()) label else text
    GlassPill(
        text = displayLabel,
        icon = icon,
        active = selected,
        accentColor = activeColor,
        onClick = onClick,
        modifier = modifier
    )
}
