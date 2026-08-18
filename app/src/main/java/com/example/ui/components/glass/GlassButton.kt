package com.example.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.animation.pressScale
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate900

enum class GlassButtonVariant {
    PrimaryAccent,
    SolidDark,
    TranslucentGlass,
    Destructive
}

/**
 * Tactile Liquid Glass Button with spring compression and specular border highlight.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GlassButtonVariant = GlassButtonVariant.PrimaryAccent,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    height: Dp = 48.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    val isDark = isSystemInDarkTheme()

    val backgroundBrush = when (variant) {
        GlassButtonVariant.PrimaryAccent -> Brush.verticalGradient(
            colors = listOf(
                PolishBluePrimary,
                PolishBlueDark
            )
        )
        GlassButtonVariant.SolidDark -> Brush.verticalGradient(
            colors = listOf(
                PolishSlate900,
                Color(0xFF020617)
            )
        )
        GlassButtonVariant.TranslucentGlass -> {
            val topColor = if (isDark) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.90f)
            val bottomColor = if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f)
            Brush.verticalGradient(colors = listOf(topColor, bottomColor))
        }
        GlassButtonVariant.Destructive -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFEF4444),
                Color(0xFFDC2626)
            )
        )
    }

    val contentColor = when (variant) {
        GlassButtonVariant.PrimaryAccent -> Color.White
        GlassButtonVariant.SolidDark -> Color.White
        GlassButtonVariant.TranslucentGlass -> if (isDark) Color.White else PolishSlate900
        GlassButtonVariant.Destructive -> Color.White
    }

    val borderStroke = when (variant) {
        GlassButtonVariant.PrimaryAccent -> Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.10f)
            )
        )
        GlassButtonVariant.SolidDark -> Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.20f),
                Color.Transparent
            )
        )
        GlassButtonVariant.TranslucentGlass -> Brush.verticalGradient(
            colors = listOf(
                if (isDark) Color.White.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.90f),
                if (isDark) Color.White.copy(alpha = 0.05f) else PolishBluePrimary.copy(alpha = 0.15f)
            )
        )
        GlassButtonVariant.Destructive -> Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.40f),
                Color.Transparent
            )
        )
    }

    Box(
        modifier = modifier
            .height(height)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = shape,
                ambientColor = PolishBluePrimary.copy(alpha = 0.10f),
                spotColor = PolishBluePrimary.copy(alpha = 0.15f)
            )
            .pressScale(scaleDown = 0.96f, onClick = if (enabled) onClick else { {} })
            .clip(shape)
            .background(backgroundBrush)
            .border(width = 1.dp, brush = borderStroke, shape = shape)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = contentColor
            )
        }
    }
}

/**
 * Floating Glass circular icon button (for Camera flash, gallery, close, options).
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 48.dp,
    iconSize: Dp = 22.dp,
    tint: Color? = null
) {
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.85f)
    val defaultTint = if (isDark) Color.White else PolishSlate900
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            if (isDark) Color.White.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.95f),
            if (isDark) Color.White.copy(alpha = 0.05f) else PolishBluePrimary.copy(alpha = 0.15f)
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.15f))
            .pressScale(scaleDown = 0.92f, onClick = onClick)
            .clip(CircleShape)
            .background(bg)
            .border(width = 1.dp, brush = borderBrush, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint ?: defaultTint,
            modifier = Modifier.size(iconSize)
        )
    }
}
