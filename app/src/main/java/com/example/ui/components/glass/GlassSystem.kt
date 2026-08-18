package com.example.ui.components.glass

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AtmosphereGlowBlue
import com.example.ui.theme.AtmosphereGlowCyan
import com.example.ui.theme.AtmosphereGlowEmerald
import com.example.ui.theme.AtmosphereGlowIndigo
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassHighlightDark
import com.example.ui.theme.GlassHighlightLight
import com.example.ui.theme.GlassSoftDark
import com.example.ui.theme.GlassSoftLight
import com.example.ui.theme.GlassSolidDark
import com.example.ui.theme.GlassSolidLight
import com.example.ui.theme.GlassStrongDark
import com.example.ui.theme.GlassStrongLight
import com.example.ui.theme.GlassUltraDark
import com.example.ui.theme.GlassUltraLight
import com.example.ui.theme.PolishBackground
import com.example.ui.theme.PolishBluePrimary

/**
 * Hierarchy levels for Liquid Glass materials.
 * ULTRA: High translucency for floating bars, floating overlays, search pills.
 * STRONG: Moderate translucency for main hero cards, next reminder, bottom sheets, modals.
 * SOFT: Subtle translucency for secondary cards, lists, group items, chat bubbles.
 * SOLID: Maximum opacity for dense medical info, side effects, labels where readability is paramount.
 */
enum class GlassLevel {
    Ultra,
    Strong,
    Soft,
    Solid
}

/**
 * Retrieves the surface color for the specified [GlassLevel] and theme.
 */
@Composable
fun getGlassSurfaceColor(
    level: GlassLevel,
    isDark: Boolean = isSystemInDarkTheme(),
    tint: Color? = null
): Color {
    val base = if (isDark) {
        when (level) {
            GlassLevel.Ultra -> GlassUltraDark
            GlassLevel.Strong -> GlassStrongDark
            GlassLevel.Soft -> GlassSoftDark
            GlassLevel.Solid -> GlassSolidDark
        }
    } else {
        when (level) {
            GlassLevel.Ultra -> GlassUltraLight
            GlassLevel.Strong -> GlassStrongLight
            GlassLevel.Soft -> GlassSoftLight
            GlassLevel.Solid -> GlassSolidLight
        }
    }
    return if (tint != null) {
        // Blend subtle tint with glass surface
        Color(
            red = (base.red * 0.85f + tint.red * 0.15f).coerceIn(0f, 1f),
            green = (base.green * 0.85f + tint.green * 0.15f).coerceIn(0f, 1f),
            blue = (base.blue * 0.85f + tint.blue * 0.15f).coerceIn(0f, 1f),
            alpha = base.alpha
        )
    } else {
        base
    }
}

/**
 * Produces a refined specular highlight border stroke for Liquid Glass surfaces.
 */
@Composable
fun getGlassBorderStroke(
    level: GlassLevel,
    isDark: Boolean = isSystemInDarkTheme(),
    borderWidth: Dp = 1.dp
): BorderStroke {
    val (startColor, endColor) = if (isDark) {
        when (level) {
            GlassLevel.Ultra -> Color.White.copy(alpha = 0.22f) to Color.White.copy(alpha = 0.05f)
            GlassLevel.Strong -> Color.White.copy(alpha = 0.18f) to Color.White.copy(alpha = 0.04f)
            GlassLevel.Soft -> Color.White.copy(alpha = 0.12f) to Color.White.copy(alpha = 0.03f)
            GlassLevel.Solid -> GlassBorderDark to GlassBorderDark.copy(alpha = 0.5f)
        }
    } else {
        when (level) {
            GlassLevel.Ultra -> Color.White.copy(alpha = 0.85f) to PolishBluePrimary.copy(alpha = 0.12f)
            GlassLevel.Strong -> Color.White.copy(alpha = 0.75f) to PolishBluePrimary.copy(alpha = 0.10f)
            GlassLevel.Soft -> Color.White.copy(alpha = 0.60f) to PolishBluePrimary.copy(alpha = 0.08f)
            GlassLevel.Solid -> PolishBluePrimary.copy(alpha = 0.12f) to PolishBluePrimary.copy(alpha = 0.06f)
        }
    }

    val brush = Brush.verticalGradient(
        colors = listOf(startColor, endColor)
    )
    return BorderStroke(borderWidth, brush)
}

/**
 * Modifier that applies the Liquid Glass visual formula:
 * 1. Soft elevation shadow
 * 2. Shape clip
 * 3. Translucent surface with subtle specular gradient
 * 4. Specular border highlight
 */
@Composable
fun Modifier.glassEffect(
    level: GlassLevel = GlassLevel.Strong,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = when (level) {
        GlassLevel.Ultra -> 8.dp
        GlassLevel.Strong -> 6.dp
        GlassLevel.Soft -> 2.dp
        GlassLevel.Solid -> 2.dp
    },
    borderWidth: Dp = 1.dp,
    tint: Color? = null,
    isDark: Boolean = isSystemInDarkTheme()
): Modifier {
    val surfaceColor = getGlassSurfaceColor(level, isDark, tint)
    val highlightColor = if (isDark) GlassHighlightDark else GlassHighlightLight
    val borderStroke = getGlassBorderStroke(level, isDark, borderWidth)

    return this
        .then(
            if (elevation > 0.dp) {
                Modifier.shadow(
                    elevation = elevation,
                    shape = shape,
                    ambientColor = if (isDark) Color.Black.copy(alpha = 0.6f) else PolishBluePrimary.copy(alpha = 0.08f),
                    spotColor = if (isDark) Color.Black.copy(alpha = 0.7f) else PolishBluePrimary.copy(alpha = 0.12f)
                )
            } else Modifier
        )
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    surfaceColor,
                    surfaceColor.copy(alpha = (surfaceColor.alpha * 0.96f).coerceIn(0f, 1f))
                )
            )
        )
        .border(borderStroke, shape)
}

/**
 * Calm Healthcare Atmospheric Background:
 * Renders very subtle, non-intrusive organic glows and soft gradients
 * behind the content so the Liquid Glass translucency layer shines with depth.
 */
@Composable
fun GlassAtmosphericBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val baseBg = if (isDark) DarkBackground else PolishBackground
    val glow1 = if (isDark) AtmosphereGlowBlue.copy(alpha = 0.08f) else AtmosphereGlowBlue
    val glow2 = if (isDark) AtmosphereGlowCyan.copy(alpha = 0.06f) else AtmosphereGlowCyan
    val glow3 = if (isDark) AtmosphereGlowEmerald.copy(alpha = 0.05f) else AtmosphereGlowEmerald
    val glow4 = if (isDark) AtmosphereGlowIndigo.copy(alpha = 0.06f) else AtmosphereGlowIndigo

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Top right soft blue atmospheric glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow1, Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.10f),
                        radius = w * 0.75f
                    ),
                    radius = w * 0.75f,
                    center = Offset(w * 0.85f, h * 0.10f)
                )

                // Middle left soft cyan accent
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow2, Color.Transparent),
                        center = Offset(w * 0.10f, h * 0.45f),
                        radius = w * 0.65f
                    ),
                    radius = w * 0.65f,
                    center = Offset(w * 0.10f, h * 0.45f)
                )

                // Bottom right subtle emerald health glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow3, Color.Transparent),
                        center = Offset(w * 0.90f, h * 0.80f),
                        radius = w * 0.70f
                    ),
                    radius = w * 0.70f,
                    center = Offset(w * 0.90f, h * 0.80f)
                )

                // Top left subtle indigo tint
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow4, Color.Transparent),
                        center = Offset(w * 0.15f, h * 0.05f),
                        radius = w * 0.50f
                    ),
                    radius = w * 0.50f,
                    center = Offset(w * 0.15f, h * 0.05f)
                )
            }
    ) {
        content()
    }
}
