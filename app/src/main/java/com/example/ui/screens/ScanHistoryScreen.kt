package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassLevel
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.StatusAlert
import com.example.ui.viewmodel.HealthMateViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanHistoryScreen(
    viewModel: HealthMateViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val history by viewModel.scanHistory.collectAsStateWithLifecycle()

    val sdf = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault())

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Scan History",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) Color.White else PolishSlate900
                            )
                        }
                    },
                    actions = {
                        if (history.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearAllData() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear History", tint = StatusAlert)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            if (history.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (isDark) PolishSlate400 else PolishSlate400
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No Scan History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Your recent AI package scan history will appear here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) PolishSlate400 else PolishSlate500
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(history) { scan ->
                        GlassCard(
                            level = GlassLevel.Soft,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = PolishBluePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = scan.detectedName.ifBlank { "Unidentified Package" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else PolishSlate900
                                    )
                                    Text(
                                        text = sdf.format(Date(scan.scannedAt)),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                    Text(
                                        text = "${scan.strength} • ${scan.form}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PolishBluePrimary
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteScanHistory(scan.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete entry",
                                        tint = if (isDark) PolishSlate400 else PolishSlate400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
