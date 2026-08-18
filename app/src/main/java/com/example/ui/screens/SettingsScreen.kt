package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassModal
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate700
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.StatusAlert
import com.example.ui.viewmodel.HealthMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: HealthMateViewModel,
    onNavigateToScanHistory: () -> Unit,
    onNavigateToAiAssistant: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showDisclaimerModal by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showResetPersonalizationDialog by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAccountSwitchDialog by remember { mutableStateOf(false) }
    var showNewUserDialog by remember { mutableStateOf(false) }

    var editName by remember(currentUser) { mutableStateOf(currentUser?.displayName ?: "") }
    var editEmail by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }

    var newUserName by remember { mutableStateOf("") }
    var newUserEmail by remember { mutableStateOf("") }

    val displayName = currentUser?.displayName?.ifBlank { "Account User" } ?: "Account User"
    val userEmail = currentUser?.email?.ifBlank { "user@example.com" } ?: "user@example.com"
    val initials = displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").uppercase().ifEmpty { "U" }

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Profile & Settings",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Authenticated User Profile Card (Ultra Glass)
                item {
                    GlassCard(
                        level = GlassLevel.Ultra,
                        tint = PolishSlate900,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(PolishBluePrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = userEmail,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PolishSlate300
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ID: ${currentUser?.id ?: "guest"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = PolishBlueContainer
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        editName = currentUser?.displayName ?: ""
                                        editEmail = currentUser?.email ?: ""
                                        showEditProfileDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }

                // Personal Information
                item {
                    Text(
                        "Personal Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                            SettingActionRow(
                                icon = Icons.Default.Person,
                                title = "Your name",
                                subtitle = if (displayName.isNotBlank()) displayName else "Not set",
                                onClick = {
                                    editName = currentUser?.displayName ?: ""
                                    editEmail = currentUser?.email ?: ""
                                    showEditProfileDialog = true
                                }
                            )
                        }
                    }
                }

                // 2. Account & Multi-User Management
                item {
                    Text(
                        "Account & Switcher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                            SettingActionRow(
                                icon = Icons.Default.SwitchAccount,
                                title = "Switch Account",
                                subtitle = "Signed in as $displayName (${allUsers.size} accounts on device)",
                                onClick = { showAccountSwitchDialog = true }
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Add,
                                title = "Add New Account / Sign Up",
                                subtitle = "Create a separate isolated profile with an empty cabinet",
                                onClick = { showNewUserDialog = true }
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Logout,
                                title = "Log Out",
                                subtitle = "Sign out from active session",
                                onClick = { showAccountSwitchDialog = true },
                                tint = StatusAlert
                            )
                        }
                    }
                }

                // 3. General & Preferences Section
                item {
                    Text(
                        "Preferences & Security",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                            SettingToggleRow(
                                icon = Icons.Default.Notifications,
                                title = "In-App Medicine Reminders",
                                subtitle = "Show scheduled medication notifications",
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Psychology,
                                title = "AI Health Assistant (AI Mode)",
                                subtitle = "Chat about packaging terms, batch numbers & guidelines",
                                onClick = onNavigateToAiAssistant
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.History,
                                title = "AI Scan History",
                                subtitle = "View previous package scans for $displayName",
                                onClick = onNavigateToScanHistory
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.CameraAlt,
                                title = "Camera Permissions",
                                subtitle = "Granted for medicine package scanning",
                                onClick = {}
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Lock,
                                title = "Privacy & User Data Isolation",
                                subtitle = "Data isolated by User ID (${currentUser?.id ?: "active"})",
                                onClick = {}
                            )
                        }
                    }
                }

                // 4. Legal & Disclaimer
                item {
                    Text(
                        "Medical Disclaimer & About",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                            SettingActionRow(
                                icon = Icons.Default.Shield,
                                title = "Full Medical Disclaimer",
                                subtitle = "Read health & AI safety guidelines",
                                onClick = { showDisclaimerModal = true }
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Info,
                                title = "About HealthMate AI",
                                subtitle = "Version 2.0.0 • Multi-User Smart Medicine Companion",
                                onClick = {}
                            )
                        }
                    }
                }

                // 5. Data Management Section
                item {
                    Text(
                        "Data Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                item {
                    GlassCard(
                        level = GlassLevel.Soft,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
                            SettingActionRow(
                                icon = Icons.Default.Person,
                                title = "Reset Personalization",
                                subtitle = "Remove saved name and return to first-launch welcome screen",
                                onClick = { showResetPersonalizationDialog = true }
                            )

                            HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100)

                            SettingActionRow(
                                icon = Icons.Default.Delete,
                                title = "Clear Data for $displayName",
                                subtitle = "Permanently clear medicines & reminders for current account",
                                onClick = { showClearDataDialog = true },
                                tint = StatusAlert
                            )
                        }
                    }
                }

                item {
                    SafetyDisclaimerBanner()
                }
            }
        }
    }

    // Edit Profile Modal
    if (showEditProfileDialog) {
        GlassModal(
            onDismissRequest = { showEditProfileDialog = false },
            title = "Edit User Profile",
            icon = Icons.Default.Person,
            confirmButton = {
                GlassButton(
                    text = "Save Changes",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        viewModel.updateUserProfile(editName, editEmail)
                        showEditProfileDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Update display name for Home greeting and profile identity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
                GlassInput(
                    value = editName,
                    onValueChange = { editName = it },
                    placeholder = "Display Name (e.g. Maya)",
                    label = "Display Name",
                    singleLine = true
                )
                GlassInput(
                    value = editEmail,
                    onValueChange = { editEmail = it },
                    placeholder = "Email Address",
                    label = "Email Address",
                    singleLine = true
                )
            }
        }
    }

    // Switch Account Modal
    if (showAccountSwitchDialog) {
        GlassModal(
            onDismissRequest = { showAccountSwitchDialog = false },
            title = "Switch Active Account",
            icon = Icons.Default.SwitchAccount,
            confirmButton = {
                GlassButton(
                    text = "+ Add Account",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        showAccountSwitchDialog = false
                        showNewUserDialog = true
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showAccountSwitchDialog = false }) {
                    Text("Close", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Select an account on this device to switch active data session:",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
                allUsers.forEach { user ->
                    val isSelected = user.id == currentUser?.id
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PolishBlueContainer.copy(alpha = 0.6f)
                                else if (isDark) Color.White.copy(alpha = 0.05f)
                                else PolishSlate100.copy(alpha = 0.6f)
                            )
                            .clickable {
                                viewModel.switchUser(user.id)
                                showAccountSwitchDialog = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PolishBluePrimary else PolishSlate400),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.displayName.take(1).uppercase().ifEmpty { "U" },
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.displayName.ifBlank { "User ${user.id}" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                            Text(
                                text = user.email.ifBlank { "user@example.com" },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = "Active", tint = PolishBluePrimary)
                        }
                    }
                }
            }
        }
    }

    // Add New Account Modal
    if (showNewUserDialog) {
        GlassModal(
            onDismissRequest = { showNewUserDialog = false },
            title = "New Account",
            icon = Icons.Default.Add,
            confirmButton = {
                GlassButton(
                    text = "Create Account",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        if (newUserName.isNotBlank() || newUserEmail.isNotBlank()) {
                            viewModel.createOrSwitchUser(newUserEmail, newUserName)
                            newUserName = ""
                            newUserEmail = ""
                            showNewUserDialog = false
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showNewUserDialog = false }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Enter name and email for the new user account. New accounts start with an empty cabinet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
                GlassInput(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    placeholder = "Full Name (e.g. Priya Patel)",
                    label = "Full Name",
                    singleLine = true
                )
                GlassInput(
                    value = newUserEmail,
                    onValueChange = { newUserEmail = it },
                    placeholder = "Email Address",
                    label = "Email Address",
                    singleLine = true
                )
            }
        }
    }

    // Full Medical Disclaimer Modal
    if (showDisclaimerModal) {
        GlassModal(
            onDismissRequest = { showDisclaimerModal = false },
            title = "Medical Disclaimer",
            icon = Icons.Default.Shield,
            iconTint = PolishBluePrimary,
            confirmButton = {
                GlassButton(
                    text = "I Understand",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = { showDisclaimerModal = false }
                )
            }
        ) {
            Text(
                "This app is designed to help organize medication information and reminders. AI-generated information may be incomplete or incorrect. Always verify medicine information against physical packaging and follow instructions from your healthcare professional. This app does not diagnose conditions or prescribe, change, or replace medical treatment.",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                color = if (isDark) PolishSlate300 else PolishSlate700
            )
        }
    }

    // Clear Data Confirmation Modal
    if (showClearDataDialog) {
        GlassModal(
            onDismissRequest = { showClearDataDialog = false },
            title = "Clear Data for $displayName",
            icon = Icons.Default.Delete,
            iconTint = StatusAlert,
            confirmButton = {
                GlassButton(
                    text = "Delete Data",
                    variant = GlassButtonVariant.Destructive,
                    height = 42.dp,
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Text(
                "Are you sure you want to permanently delete medicines, reminders, scan history, and logs for $displayName?",
                color = if (isDark) PolishSlate300 else PolishSlate500,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Reset Personalization Confirmation Modal
    if (showResetPersonalizationDialog) {
        GlassModal(
            onDismissRequest = { showResetPersonalizationDialog = false },
            title = "Reset Personalization",
            icon = Icons.Default.Person,
            confirmButton = {
                GlassButton(
                    text = "Reset",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        viewModel.resetPersonalization()
                        showResetPersonalizationDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResetPersonalizationDialog = false }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Text(
                "Remove your saved name and return to the first-launch welcome screen? Your saved medicines and reminders will not be deleted.",
                color = if (isDark) PolishSlate300 else PolishSlate500,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PolishBluePrimary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PolishBluePrimary
            )
        )
    }
}

@Composable
private fun SettingActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = PolishBluePrimary
) {
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (tint == StatusAlert) tint else if (isDark) Color.White else PolishSlate900
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (isDark) PolishSlate500 else PolishSlate300)
    }
}
