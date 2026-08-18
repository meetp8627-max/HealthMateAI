package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBlueDark
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishEmeraldBg
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.StatusAlert

@Composable
fun OnboardingScreen(
    onNameSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        runCatching {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    fun submitName() {
        val trimmed = nameInput.trim()
        if (trimmed.isBlank()) {
            errorMessage = "Please enter your name to continue"
        } else {
            errorMessage = null
            keyboardController?.hide()
            onNameSubmitted(trimmed)
        }
    }

    GlassAtmosphericBackground(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Header
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = "HealthMate",
                            tint = PolishBluePrimary,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "HealthMate AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else PolishBlueDark,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "Your medication, organized beautifully.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDark) PolishSlate400 else PolishSlate500,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Onboarding Card (Ultra Glass)
                    GlassCard(
                        level = GlassLevel.Ultra,
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Welcome to HealthMate 👋",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "What should we call you?",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDark) Color.White else PolishSlate900,
                                letterSpacing = (-0.5).sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            GlassInput(
                                value = nameInput,
                                onValueChange = { input ->
                                    if (input.length <= 30) {
                                        nameInput = input
                                        if (errorMessage != null) errorMessage = null
                                    }
                                },
                                placeholder = "Your name",
                                singleLine = true,
                                isError = errorMessage != null,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { submitName() }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester)
                            )

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = errorMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StatusAlert,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            GlassButton(
                                text = "Continue",
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                variant = GlassButtonVariant.PrimaryAccent,
                                height = 52.dp,
                                onClick = { submitName() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) PolishEmeraldBg.copy(alpha = 0.3f) else PolishEmeraldBg)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🔒 Private & stored locally on your device",
                            style = MaterialTheme.typography.labelSmall,
                            color = PolishEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
