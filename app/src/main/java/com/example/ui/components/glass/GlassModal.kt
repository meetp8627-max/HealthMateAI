package com.example.ui.components.glass

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900

/**
 * Liquid Glass Modal Dialog with backdrop dimming, specular border highlights,
 * and smooth natural spring scale transitions.
 */
@Composable
fun GlassModal(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        // Dim Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = if (isDark) 0.65f else 0.40f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismissRequest
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glass Modal Container
            GlassCard(
                level = GlassLevel.Strong,
                shape = RoundedCornerShape(28.dp),
                elevation = 16.dp,
                borderWidth = 1.dp,
                pressEffect = false,
                modifier = modifier
                    .fillMaxWidth(0.92f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Prevent dismiss on card tap
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (icon != null) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconTint ?: (if (isDark) Color.White else PolishBluePrimary),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900,
                            letterSpacing = (-0.3).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    content()

                    if (confirmButton != null || dismissButton != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (dismissButton != null) {
                                dismissButton()
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            if (confirmButton != null) {
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }
    }
}
