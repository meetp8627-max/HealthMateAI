package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MedicineEntity
import com.example.data.model.MedicineKnowledgeEntity
import com.example.data.model.ReminderEntity
import com.example.ui.animation.pressScale
import com.example.ui.animation.staggeredEntrance
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassChip
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassModal
import com.example.ui.components.glass.GlassPill
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishEmerald
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate700
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyAmberOnContainer
import com.example.ui.theme.StatusAlert
import com.example.ui.viewmodel.HealthMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: Long,
    viewModel: HealthMateViewModel,
    onNavigateBack: () -> Unit,
    onCreateReminder: (Long, String) -> Unit,
    onNavigateToAiAssistant: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    var medicine by remember { mutableStateOf<MedicineEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrescriptionReasonDialog by remember { mutableStateOf(false) }
    var isSimplifiedMode by remember { mutableStateOf(false) }
    var isSimplifying by remember { mutableStateOf(false) }

    val allMeds by viewModel.allMedicines.collectAsStateWithLifecycle()
    val reminders by viewModel.activeReminders.collectAsStateWithLifecycle()
    val knowledge by viewModel.getMedicineKnowledgeFlow(medicineId).collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(medicineId, allMeds) {
        val found = allMeds.firstOrNull { it.id == medicineId }
        medicine = found
        if (found != null && knowledge == null) {
            viewModel.getOrFetchMedicineKnowledge(found)
        }
    }

    val medicineReminders = reminders.filter { it.medicineId == medicineId }

    // Section expansion state
    var isHowItWorksExpanded by remember { mutableStateOf(false) }
    var isCommonUsesExpanded by remember { mutableStateOf(false) }
    var isImportantSafetyExpanded by remember { mutableStateOf(false) }
    var isSideEffectsExpanded by remember { mutableStateOf(false) }
    var isScheduleExpanded by remember { mutableStateOf(true) }
    var isPackageInfoExpanded by remember { mutableStateOf(true) }

    val uriHandler = LocalUriHandler.current

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            medicine?.name ?: "Medicine Details",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else PolishSlate900,
                            maxLines = 1
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
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Medicine", tint = StatusAlert)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            val med = medicine
            if (med == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = PolishBluePrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading medicine details...", color = PolishSlate500)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Medicine Header
                    item {
                        MedicineHeaderCard(
                            medicine = med,
                            knowledge = knowledge,
                            modifier = Modifier.staggeredEntrance(0)
                        )
                    }

                    // 2. "What is it used for?" Card (Prominent & Primary Liquid Glass)
                    item {
                        if (med.confidence == "Low" || !med.verified) {
                            UnverifiedWarningCard(
                                onScanAgain = onNavigateToScan,
                                onAskAi = onNavigateToAiAssistant,
                                modifier = Modifier.staggeredEntrance(1)
                            )
                        } else if (knowledge != null) {
                            WhatIsItUsedForCard(
                                knowledge = knowledge!!,
                                isSimplifiedMode = isSimplifiedMode,
                                isSimplifying = isSimplifying,
                                onToggleSimplify = {
                                    if (!isSimplifiedMode) {
                                        if (knowledge?.simplifiedSummary != null) {
                                            isSimplifiedMode = true
                                        } else {
                                            isSimplifying = true
                                            viewModel.simplifyMedicineExplanation(med.id, knowledge!!.shortSummary) { result ->
                                                isSimplifying = false
                                                if (result != null) {
                                                    isSimplifiedMode = true
                                                }
                                            }
                                        }
                                    } else {
                                        isSimplifiedMode = false
                                    }
                                },
                                onAskPrescriptionReason = {
                                    showPrescriptionReasonDialog = true
                                },
                                modifier = Modifier.staggeredEntrance(1)
                            )
                        } else {
                            UnmatchedKnowledgeCard(
                                medicineName = med.name,
                                onAskAi = onNavigateToAiAssistant,
                                onScanAgain = onNavigateToScan,
                                modifier = Modifier.staggeredEntrance(1)
                            )
                        }
                    }

                    // 3. Collapsible: How does it work?
                    if (knowledge != null) {
                        item {
                            CollapsibleInfoCard(
                                title = "How does it work?",
                                icon = Icons.Default.Science,
                                isExpanded = isHowItWorksExpanded,
                                onToggle = { isHowItWorksExpanded = !isHowItWorksExpanded },
                                modifier = Modifier.staggeredEntrance(2)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = knowledge!!.mechanism.ifBlank { "How this medicine works could not be verified." },
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                        color = if (isDark) PolishSlate300 else PolishSlate700
                                    )
                                    if (knowledge!!.isCombination) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(PolishBlueContainer.copy(alpha = 0.5f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Info, contentDescription = null, tint = PolishBluePrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Contains multiple active ingredients working together.",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = PolishBluePrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 4. Collapsible: Common uses
                        item {
                            CollapsibleInfoCard(
                                title = "Common uses",
                                icon = Icons.Default.Healing,
                                isExpanded = isCommonUsesExpanded,
                                onToggle = { isCommonUsesExpanded = !isCommonUsesExpanded },
                                modifier = Modifier.staggeredEntrance(3)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "General conditions this medication is approved or commonly prescribed for:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                    knowledge!!.commonUses.forEach { useText ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 7.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(PolishBluePrimary)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = useText,
                                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                                color = if (isDark) Color.White else PolishSlate900
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Collapsible: Important safety information
                        item {
                            CollapsibleInfoCard(
                                title = "Important safety information",
                                icon = Icons.Default.Warning,
                                iconTint = SafetyAmberOnContainer,
                                isExpanded = isImportantSafetyExpanded,
                                onToggle = { isImportantSafetyExpanded = !isImportantSafetyExpanded },
                                modifier = Modifier.staggeredEntrance(4)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    knowledge!!.importantSafetyInfo.forEach { safetyNote ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .size(16.dp),
                                                tint = SafetyAmberOnContainer
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = safetyNote,
                                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                                color = if (isDark) Color.White else PolishSlate900
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 6. Collapsible: Common side effects
                        item {
                            CollapsibleInfoCard(
                                title = "Common side effects",
                                icon = Icons.Default.Psychology,
                                isExpanded = isSideEffectsExpanded,
                                onToggle = { isSideEffectsExpanded = !isSideEffectsExpanded },
                                modifier = Modifier.staggeredEntrance(5)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    knowledge!!.commonSideEffects.forEach { effect ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 7.dp)
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(if (isDark) PolishSlate400 else PolishSlate400)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = effect,
                                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                                color = if (isDark) Color.White else PolishSlate900
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "ℹ️ Side effects vary by medicine and person. Consult a healthcare professional or pharmacist if symptoms persist or concern you.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                }
                            }
                        }
                    }

                    // 7. Your Medication Schedule (Default Expanded)
                    item {
                        CollapsibleInfoCard(
                            title = "Your schedule",
                            icon = Icons.Default.Schedule,
                            isExpanded = isScheduleExpanded,
                            onToggle = { isScheduleExpanded = !isScheduleExpanded },
                            action = {
                                GlassButton(
                                    text = if (medicineReminders.isEmpty()) "Set Reminder" else "Add Dose",
                                    icon = Icons.Default.Add,
                                    variant = GlassButtonVariant.PrimaryAccent,
                                    height = 36.dp,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    onClick = { onCreateReminder(med.id, med.name) }
                                )
                            },
                            modifier = Modifier.staggeredEntrance(6)
                        ) {
                            if (medicineReminders.isEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else PolishSlate100.copy(alpha = 0.5f))
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = PolishSlate400)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "No active reminder scheduled for this medicine. Tap 'Set Reminder' to receive daily dose alerts.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    medicineReminders.forEach { rem ->
                                        ScheduleReminderRow(rem = rem)
                                    }
                                }
                            }
                        }
                    }

                    // 8. From Your Package Card (Default Expanded)
                    item {
                        CollapsibleInfoCard(
                            title = "From your package",
                            icon = Icons.Default.Inventory2,
                            isExpanded = isPackageInfoExpanded,
                            onToggle = { isPackageInfoExpanded = !isPackageInfoExpanded },
                            modifier = Modifier.staggeredEntrance(7)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else PolishSlate100.copy(alpha = 0.6f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = PolishSlate500, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Information extracted from your medicine package. Package data ≠ General medicine information.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 14.sp),
                                        color = if (isDark) PolishSlate400 else PolishSlate500
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                DetailRow("Medicine Name", med.name)
                                if (med.brandName.isNotBlank()) {
                                    DetailRow("Brand Name", med.brandName)
                                }
                                DetailRow("Strength", med.strength.ifBlank { "Not specified" })
                                DetailRow("Dosage Form", med.form.ifBlank { "Tablet" })
                                DetailRow("Manufacturer", med.manufacturer.ifBlank { "Not specified" })
                                DetailRow("Expiry Date", med.expiryDate.ifBlank { "Not specified" })
                                DetailRow("Batch / Lot #", med.batchNumber.ifBlank { "Not specified" })
                                DetailRow("Package Quantity", med.quantity.ifBlank { "Not specified" })
                                if (med.activeIngredients.isNotEmpty()) {
                                    DetailRow("Active Ingredients", med.activeIngredients.joinToString())
                                }
                                if (med.notes.isNotBlank()) {
                                    DetailRow("Notes / Warnings", med.notes)
                                }
                            }
                        }
                    }

                    // 9. Source Information Card
                    if (knowledge != null) {
                        item {
                            SourceInformationCard(
                                sourceName = knowledge!!.sourceName,
                                sourceUpdatedAt = knowledge!!.sourceUpdatedAt,
                                sourceUrl = knowledge!!.sourceUrl,
                                onOpenUrl = {
                                    try {
                                        uriHandler.openUri(knowledge!!.sourceUrl)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier.staggeredEntrance(8)
                            )
                        }
                    }

                    // 10. Safety Disclaimer Banner
                    item {
                        SafetyDisclaimerBanner(
                            text = "Medication information is provided for general educational purposes. It does not confirm why this medicine was prescribed to you and does not replace advice from a doctor or pharmacist. Follow the instructions on your medicine label and from your healthcare professional.",
                            modifier = Modifier.staggeredEntrance(9)
                        )
                    }
                }
            }
        }
    }

    // Prescription Reason Liquid Glass Modal
    if (showPrescriptionReasonDialog) {
        GlassModal(
            onDismissRequest = { showPrescriptionReasonDialog = false },
            title = "Why was this prescribed to me?",
            icon = Icons.Default.LocalPharmacy,
            iconTint = PolishBluePrimary,
            confirmButton = {
                GlassButton(
                    text = "View Package Info",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 42.dp,
                    onClick = {
                        showPrescriptionReasonDialog = false
                        isPackageInfoExpanded = true
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPrescriptionReasonDialog = false
                        onNavigateToAiAssistant()
                    }
                ) {
                    Text("Ask HealthMate AI", color = PolishBluePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "This medicine can be used for several medical conditions.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color.White else PolishSlate900
                )
                Text(
                    "Only your healthcare professional or your official prescription label instructions can confirm why it was prescribed to you specifically.",
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = if (isDark) PolishSlate300 else PolishSlate700
                )
                Text(
                    "HealthMate AI does not diagnose conditions, infer individual clinical reasons, or recommend changing doses.",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
            }
        }
    }

    // Delete Medicine Confirmation Modal
    if (showDeleteDialog && medicine != null) {
        GlassModal(
            onDismissRequest = { showDeleteDialog = false },
            title = "Delete Medicine",
            icon = Icons.Default.Delete,
            iconTint = StatusAlert,
            confirmButton = {
                GlassButton(
                    text = "Delete",
                    variant = GlassButtonVariant.Destructive,
                    height = 42.dp,
                    onClick = {
                        viewModel.deleteMedicine(medicineId)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = if (isDark) PolishSlate400 else PolishSlate500)
                }
            }
        ) {
            Text(
                "Are you sure you want to delete '${medicine?.name}'? This will also remove associated reminders.",
                color = if (isDark) PolishSlate300 else PolishSlate500,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// ---------------- UI COMPONENTS ---------------- //

@Composable
private fun MedicineHeaderCard(
    medicine: MedicineEntity,
    knowledge: MedicineKnowledgeEntity?,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Ultra,
        tint = PolishSlate900,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PolishBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = PolishBluePrimary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        medicine.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (medicine.brandName.isNotBlank() && !medicine.name.equals(medicine.brandName, ignoreCase = true)) {
                        Text(
                            "Brand: ${medicine.brandName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PolishSlate300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${medicine.strength.ifBlank { "Standard Strength" }} · ${medicine.form.ifBlank { "Tablet" }}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PolishBluePrimary
                )

                if (knowledge != null) {
                    GlassPill(
                        text = "Verified Record",
                        icon = Icons.Default.VerifiedUser,
                        active = true,
                        accentColor = PolishBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun WhatIsItUsedForCard(
    knowledge: MedicineKnowledgeEntity,
    isSimplifiedMode: Boolean,
    isSimplifying: Boolean,
    onToggleSimplify: () -> Unit,
    onAskPrescriptionReason: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Strong,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Icon + Title + Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PolishBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = PolishBluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "What is it used for?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                // Medicine Type Tag
                GlassPill(
                    text = knowledge.medicineType.take(24),
                    active = true,
                    accentColor = PolishBluePrimary
                )
            }

            // Summary text
            val displayText = if (isSimplifiedMode && knowledge.simplifiedSummary != null) {
                knowledge.simplifiedSummary
            } else {
                knowledge.shortSummary
            }

            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 22.sp),
                color = if (isDark) Color.White else PolishSlate900
            )

            HorizontalDivider(
                color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100,
                thickness = 1.dp
            )

            // Action Row: Explain Simply & Why Prescribed?
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Explain simply toggle
                GlassChip(
                    text = if (isSimplifying) "Simplifying..." else if (isSimplifiedMode) "✨ Plain Language (On)" else "✨ Explain simply",
                    selected = isSimplifiedMode,
                    onClick = onToggleSimplify
                )

                // "Is this why I was prescribed it?" Action Chip
                TextButton(
                    onClick = onAskPrescriptionReason,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.pressScale()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PolishBluePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Why prescribed?",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PolishBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsibleInfoCard(
    title: String,
    icon: ImageVector,
    iconTint: Color = PolishBluePrimary,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "ChevronRotation"
    )

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(animationSpec = tween(280, easing = FastOutSlowInEasing))
        ) {
            // Header Row (Clickable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle
                    )
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (action != null) {
                        action()
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = onToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = if (isDark) PolishSlate400 else PolishSlate500,
                            modifier = Modifier.rotate(rotationAngle)
                        )
                    }
                }
            }

            // Expanded Content Body
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(animationSpec = tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, bottom = 18.dp)
                ) {
                    HorizontalDivider(
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else PolishSlate100,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    content()
                }
            }
        }
    }
}

@Composable
private fun ScheduleReminderRow(rem: ReminderEntity) {
    val isDark = isSystemInDarkTheme()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isDark) Color.White.copy(alpha = 0.05f) else PolishSlate100.copy(alpha = 0.4f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = rem.scheduleType.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )
            if (rem.weekdays.isNotEmpty()) {
                Text(
                    text = "Days: ${rem.weekdays.joinToString()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
            }
        }
        Text(
            text = rem.times.joinToString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = PolishBluePrimary
        )
    }
}

@Composable
private fun SourceInformationCard(
    sourceName: String,
    sourceUpdatedAt: String,
    sourceUrl: String,
    onOpenUrl: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PolishEmerald.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PolishEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        "Information Source",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    Text(
                        "$sourceName · Updated $sourceUpdatedAt",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = if (isDark) PolishSlate400 else PolishSlate500
                    )
                }
            }

            IconButton(onClick = onOpenUrl, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "View Source",
                    tint = PolishBluePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun UnmatchedKnowledgeCard(
    medicineName: String,
    onAskAi: () -> Unit,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        level = GlassLevel.Soft,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SafetyAmberContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SafetyAmberOnContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                "Medicine information unavailable",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )

            Text(
                "We couldn't find an exact verified medication-information record for '$medicineName'. You can verify the name or ask HealthMate AI for label guidance.",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = if (isDark) PolishSlate400 else PolishSlate500,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                GlassButton(
                    text = "Scan Again",
                    icon = Icons.Default.QrCodeScanner,
                    variant = GlassButtonVariant.TranslucentGlass,
                    height = 38.dp,
                    onClick = onScanAgain
                )

                GlassButton(
                    text = "Ask HealthMate AI",
                    icon = Icons.Default.AutoAwesome,
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 38.dp,
                    onClick = onAskAi
                )
            }
        }
    }
}

@Composable
private fun UnverifiedWarningCard(
    onScanAgain: () -> Unit,
    onAskAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        level = GlassLevel.Strong,
        tint = SafetyAmberContainer,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SafetyAmberOnContainer,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Verify medicine first",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SafetyAmberOnContainer
                )
            }

            Text(
                "We need to confirm the medicine name and package details before showing general medication information. Unverified scans do not display automated medical facts.",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = SafetyAmberOnContainer
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 4.dp)) {
                GlassButton(
                    text = "Scan Again",
                    variant = GlassButtonVariant.PrimaryAccent,
                    height = 38.dp,
                    onClick = onScanAgain
                )

                GlassButton(
                    text = "Ask AI",
                    variant = GlassButtonVariant.TranslucentGlass,
                    height = 38.dp,
                    onClick = onAskAi
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) PolishSlate400 else PolishSlate500,
            modifier = Modifier.weight(0.42f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else PolishSlate900,
            modifier = Modifier.weight(0.58f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
