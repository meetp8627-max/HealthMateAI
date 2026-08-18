package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.animation.pressScale
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassChip
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.HealthMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: HealthMateViewModel,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.aiAssistantLoading.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }

    val starterPrompts = listOf(
        "What does 'mg' vs 'mL' mean on a label?",
        "What is an active ingredient?",
        "How do I read a medicine batch number?",
        "Tips for organizing daily medication"
    )

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "AI Health Assistant",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = if (isDark) Color.White else PolishSlate900
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Safety Disclaimer Top Bar
                SafetyDisclaimerBanner(
                    text = "HealthMate AI explains label terms and packaging details. It does NOT diagnose conditions or prescribe/change medications.",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubble(message = msg)
                    }

                    if (isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }
                }

                // Quick Starter Prompt Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(starterPrompts) { prompt ->
                        GlassChip(
                            text = prompt,
                            icon = Icons.Default.Psychology,
                            selected = false,
                            onClick = { viewModel.sendChatMessage(prompt) }
                        )
                    }
                }

                // Input Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassInput(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = "Ask about packaging terms...",
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(PolishBluePrimary)
                            .pressScale(scaleDown = 0.90f) {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendChatMessage(inputText)
                                    inputText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    val isDark = isSystemInDarkTheme()
    val infiniteTransition = rememberInfiniteTransition(label = "TypingDots")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "Dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "Dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "Dot3"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = PolishBluePrimary, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        GlassCard(
            level = GlassLevel.Soft,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PolishBluePrimary.copy(alpha = dot1Alpha)))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PolishBluePrimary.copy(alpha = dot2Alpha)))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PolishBluePrimary.copy(alpha = dot3Alpha)))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "HealthMate AI is typing...",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isDark = isSystemInDarkTheme()
    val isUser = message.sender == "user"

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioLowBouncy)
        ) + fadeIn(animationSpec = tween(180))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = PolishBluePrimary, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            GlassCard(
                level = if (isUser) GlassLevel.Ultra else GlassLevel.Soft,
                tint = if (isUser) PolishSlate900 else null,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                modifier = Modifier.fillMaxWidth(0.82f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = if (isUser) Color.White else if (isDark) Color.White else PolishSlate900
                    )
                }
            }
        }
    }
}
