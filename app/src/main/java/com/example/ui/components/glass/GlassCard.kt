package com.example.ui.components.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.animation.pressScale

/**
 * Reusable Liquid Glass Card supporting all depth levels (Ultra, Strong, Soft, Solid).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Strong,
    shape: Shape = RoundedCornerShape(24.dp),
    elevation: Dp = when (level) {
        GlassLevel.Ultra -> 8.dp
        GlassLevel.Strong -> 4.dp
        GlassLevel.Soft -> 2.dp
        GlassLevel.Solid -> 1.dp
    },
    borderWidth: Dp = 1.dp,
    tint: Color? = null,
    onClick: (() -> Unit)? = null,
    pressEffect: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        if (pressEffect) {
            Modifier.pressScale(scaleDown = 0.97f, onClick = onClick)
        } else {
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .then(clickModifier)
            .glassEffect(
                level = level,
                shape = shape,
                elevation = elevation,
                borderWidth = borderWidth,
                tint = tint
            ),
        content = content
    )
}
