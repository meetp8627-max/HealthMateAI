package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Ios

@Composable
fun OnboardingScreen(
    onNameSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Ios.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // App icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Ios.Blue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MonitorHeart,
                    contentDescription = "HealthMate",
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "HealthMate AI",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Ios.Label
            )

            Spacer(Modifier.height(30.dp))

            // Illustration: tinted circle + tilted routine card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(172.dp)
                        .clip(CircleShape)
                        .background(Ios.BlueTint)
                )
                Column(
                    modifier = Modifier
                        .rotate(-4f)
                        .width(244.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x14000000), spotColor = Color(0x14000000))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(horizontal = 18.dp, vertical = 18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Medication,
                            contentDescription = null,
                            tint = Ios.Blue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Your daily routine", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Ios.Label)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .then(Modifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Check, null, tint = Ios.Green, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Morning medicine", fontSize = 16.sp, color = Ios.SecondaryLabel)
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(Ios.BlueTint)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .height(5.dp)
                                .clip(CircleShape)
                                .background(Ios.Blue)
                        )
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Your medication, organized beautifully.",
                    fontSize = 34.sp,
                    lineHeight = 41.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ios.Label
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "A little clarity for your everyday routine. Keep medicines, reminders and label questions together.",
                    fontSize = 17.sp,
                    lineHeight = 24.sp,
                    color = Ios.SecondaryLabel
                )
                Spacer(Modifier.height(36.dp))
                Text(
                    text = "Welcome to HealthMate",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Ios.Label
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "What should we call you?",
                    fontSize = 18.sp,
                    color = Ios.SecondaryLabel
                )
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Ios.Separator)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = nameInput,
                    onValueChange = { input ->
                        if (input.length <= 30) {
                            nameInput = input
                            if (errorMessage != null) errorMessage = null
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 20.sp, color = Ios.Label),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { submitName() }),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        Box {
                            if (nameInput.isEmpty()) {
                                Text("Your name", fontSize = 20.sp, color = Ios.Placeholder)
                            }
                            inner()
                        }
                    }
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    fontSize = 13.sp,
                    color = Ios.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(Ios.Blue)
                    .clickable { submitName() },
                contentAlignment = Alignment.Center
            ) {
                Text("Continue", fontSize = 20.sp, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Lock, null, tint = Ios.SecondaryLabel, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Private & stored locally on your device", fontSize = 14.sp, color = Ios.SecondaryLabel)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
