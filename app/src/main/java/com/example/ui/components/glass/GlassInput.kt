package com.example.ui.components.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.StatusAlert

/**
 * Liquid Glass Input Capsule with clear contrast for editable text, optional top label, and error state.
 */
@Composable
fun GlassInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    label: String? = null,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    singleLine: Boolean = true,
    height: Dp = if (singleLine) 52.dp else 90.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val isDark = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)

    val surfaceColor = if (isDark) Color(0xCC1E293B) else Color(0xF2FFFFFF)
    val borderBrush = when {
        isError -> Brush.verticalGradient(listOf(StatusAlert, StatusAlert.copy(alpha = 0.5f)))
        isDark -> Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.04f))
        )
        else -> Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.90f), PolishBluePrimary.copy(alpha = 0.12f))
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) PolishSlate400 else PolishSlate500,
                modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = height)
                .shadow(2.dp, shape, ambientColor = PolishBluePrimary.copy(alpha = 0.05f))
                .clip(shape)
                .background(surfaceColor)
                .border(1.dp, borderBrush, shape)
                .padding(horizontal = 16.dp, vertical = if (singleLine) 0.dp else 12.dp),
            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
        ) {
            Row(
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isDark) PolishSlate400 else PolishBluePrimary,
                        modifier = Modifier
                            .padding(top = if (singleLine) 0.dp else 2.dp)
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = if (singleLine) 0.dp else 2.dp)
                ) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = if (isDark) PolishSlate400 else PolishSlate400,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }

                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = singleLine,
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else PolishSlate900,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(PolishBluePrimary),
                        keyboardOptions = keyboardOptions,
                        keyboardActions = keyboardActions,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    trailingIcon()
                }
            }
        }
    }
}
