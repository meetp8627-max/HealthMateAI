package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.components.glass.GlassAtmosphericBackground
import com.example.ui.components.glass.GlassButton
import com.example.ui.components.glass.GlassButtonVariant
import com.example.ui.components.glass.GlassCard
import com.example.ui.components.glass.GlassChip
import com.example.ui.components.glass.GlassInput
import com.example.ui.components.glass.GlassLevel
import com.example.ui.components.glass.GlassPill
import com.example.ui.components.glass.GlassPillVariant
import com.example.ui.theme.PolishBlueContainer
import com.example.ui.theme.PolishBluePrimary
import com.example.ui.theme.PolishSlate100
import com.example.ui.theme.PolishSlate300
import com.example.ui.theme.PolishSlate400
import com.example.ui.theme.PolishSlate500
import com.example.ui.theme.PolishSlate900
import com.example.ui.theme.SafetyAmberContainer
import com.example.ui.theme.SafetyAmberOnContainer
import com.example.ui.theme.StatusAlert
import com.example.ui.theme.StatusSuccess
import com.example.ui.viewmodel.HealthMateViewModel
import com.example.ui.viewmodel.ScanUiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanMedicineScreen(
    viewModel: HealthMateViewModel,
    onNavigateBack: () -> Unit,
    onContinueToReminder: (Long, String) -> Unit,
    onNavigateToMedicineDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val scanUiState by viewModel.scanUiState.collectAsStateWithLifecycle()

    var manualEntryMode by remember { mutableStateOf(false) }
    var capturedBitmapForManual by remember { mutableStateOf<Bitmap?>(null) }

    // Manual Form fields
    var manualName by remember { mutableStateOf("") }
    var manualBrand by remember { mutableStateOf("") }
    var manualStrength by remember { mutableStateOf("") }
    var manualForm by remember { mutableStateOf("Tablet") }
    var manualManufacturer by remember { mutableStateOf("") }
    var manualExpiry by remember { mutableStateOf("") }
    var manualNotes by remember { mutableStateOf("") }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    capturedBitmapForManual = bitmap
                    viewModel.analyzePackageImage(bitmap)
                }
            } catch (e: Exception) {
                // error handled quietly
            }
        }
    }

    // Camera Preview Bitmap Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            capturedBitmapForManual = it
            viewModel.analyzePackageImage(it)
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                imagePickerLauncher.launch("image/*")
            }
        } else {
            imagePickerLauncher.launch("image/*")
        }
    }

    val launchCamera = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                imagePickerLauncher.launch("image/*")
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    GlassAtmosphericBackground(modifier = modifier) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Smart Package Scanner",
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val state = scanUiState) {
                    is ScanUiState.Idle -> {
                        if (manualEntryMode) {
                            ManualEntryForm(
                                capturedBitmap = capturedBitmapForManual,
                                name = manualName, onNameChange = { manualName = it },
                                brand = manualBrand, onBrandChange = { manualBrand = it },
                                strength = manualStrength, onStrengthChange = { manualStrength = it },
                                form = manualForm, onFormChange = { manualForm = it },
                                manufacturer = manualManufacturer, onManufacturerChange = { manualManufacturer = it },
                                expiry = manualExpiry, onExpiryChange = { manualExpiry = it },
                                notes = manualNotes, onNotesChange = { manualNotes = it },
                                onSave = {
                                    viewModel.addMedicineManually(
                                        name = manualName,
                                        brand = manualBrand,
                                        strength = manualStrength,
                                        form = manualForm,
                                        manufacturer = manualManufacturer,
                                        expiry = manualExpiry,
                                        notes = manualNotes,
                                        onSaved = { id ->
                                            onContinueToReminder(id, manualName)
                                        }
                                    )
                                },
                                onCancel = { manualEntryMode = false },
                                onRetakePhoto = {
                                    manualEntryMode = false
                                    launchCamera()
                                }
                            )
                        } else {
                            CameraScanGuide(
                                onOpenCamera = { launchCamera() },
                                onUploadPhoto = { imagePickerLauncher.launch("image/*") },
                                onManualEntry = { manualEntryMode = true }
                            )
                        }
                    }

                    is ScanUiState.Scanning -> {
                        ScanningProgressView(message = state.message)
                    }

                    is ScanUiState.Verification -> {
                        capturedBitmapForManual = state.bitmap
                        VerificationScreen(
                            state = state,
                            onFieldsChanged = { name, brand, str, form, mfr, exp, notes ->
                                viewModel.updateVerificationFields(name, brand, str, form, mfr, exp, notes)
                            },
                            onConfirm = {
                                viewModel.saveVerifiedMedicine { savedId ->
                                    onContinueToReminder(savedId, state.editedName.ifBlank { "Scanned Medicine" })
                                }
                            },
                            onRescan = { viewModel.resetScanState() },
                            onOpenExisting = { existing ->
                                onNavigateToMedicineDetail(existing.id)
                            }
                        )
                    }

                    is ScanUiState.Saved -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PolishBluePrimary)
                        }
                    }

                    is ScanUiState.RateLimited -> {
                        capturedBitmapForManual = state.capturedBitmap
                        RateLimitedView(
                            initialSeconds = state.retryAfterSeconds,
                            capturedBitmap = state.capturedBitmap,
                            onRetry = {
                                val bitmap = state.capturedBitmap
                                if (bitmap != null) {
                                    viewModel.analyzePackageImage(bitmap)
                                } else {
                                    viewModel.resetScanState()
                                }
                            },
                            onManualEntry = {
                                manualEntryMode = true
                                viewModel.resetScanState()
                            }
                        )
                    }

                    is ScanUiState.Error -> {
                        capturedBitmapForManual = state.capturedBitmap
                        ScanErrorView(
                            title = state.userFriendlyTitle,
                            errorMessage = state.userFriendlyMessage,
                            onRetry = {
                                val bitmap = state.capturedBitmap
                                if (bitmap != null) {
                                    viewModel.analyzePackageImage(bitmap)
                                } else {
                                    viewModel.resetScanState()
                                }
                            },
                            onManualEntry = {
                                manualEntryMode = true
                                viewModel.resetScanState()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RateLimitedView(
    initialSeconds: Int,
    capturedBitmap: Bitmap?,
    onRetry: () -> Unit,
    onManualEntry: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var secondsLeft by remember(initialSeconds) { mutableStateOf(initialSeconds) }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            secondsLeft -= 1
        }
    }

    val minutes = secondsLeft / 60
    val secs = secondsLeft % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SafetyAmberContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = SafetyAmberOnContainer,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "AI Scan Temporarily Unavailable",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else PolishSlate900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "We've reached the AI scanning limit for now. You can try again shortly or enter the medicine details manually.",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) PolishSlate400 else PolishSlate500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassCard(
            level = GlassLevel.Soft,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AI scanning available again in",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDark) PolishSlate400 else PolishSlate500,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formattedTime,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (secondsLeft > 0) SafetyAmberOnContainer else StatusSuccess
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        GlassButton(
            text = "Try Again",
            icon = Icons.Default.Refresh,
            variant = GlassButtonVariant.PrimaryAccent,
            height = 54.dp,
            enabled = secondsLeft <= 0,
            onClick = onRetry
        )

        Spacer(modifier = Modifier.height(12.dp))

        GlassButton(
            text = "Enter Manually",
            icon = Icons.Default.Edit,
            variant = GlassButtonVariant.TranslucentGlass,
            height = 54.dp,
            onClick = onManualEntry
        )
    }
}

@Composable
private fun CameraScanGuide(
    onOpenCamera: () -> Unit,
    onUploadPhoto: () -> Unit,
    onManualEntry: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Viewfinder Frame (Ultra Glass)
        GlassCard(
            level = GlassLevel.Ultra,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = PolishBluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Place medicine package inside frame",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else PolishSlate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ensure text, drug name & strength are clearly lit",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDark) PolishSlate400 else PolishSlate500
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        GlassButton(
            text = "Open Camera & Capture",
            icon = Icons.Default.CameraAlt,
            variant = GlassButtonVariant.PrimaryAccent,
            height = 54.dp,
            onClick = onOpenCamera
        )

        Spacer(modifier = Modifier.height(12.dp))

        GlassButton(
            text = "Upload Photo from Gallery",
            icon = Icons.Default.Image,
            variant = GlassButtonVariant.TranslucentGlass,
            height = 54.dp,
            onClick = onUploadPhoto
        )

        Spacer(modifier = Modifier.height(18.dp))

        TextButton(
            onClick = onManualEntry
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, tint = PolishBluePrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Enter Medicine Information Manually", color = PolishBluePrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ScanningProgressView(message: String) {
    val isDark = isSystemInDarkTheme()
    val infiniteTransition = rememberInfiniteTransition(label = "LaserScan")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Laser Scanning Frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PolishSlate900)
                .border(2.dp, PolishBluePrimary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(72.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp)
                    .graphicsLayer {
                        translationY = laserPosition * 550f
                    }
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PolishBluePrimary,
                                Color.White,
                                PolishBluePrimary,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp,
            color = PolishBluePrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Analyzing Medicine Package",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else PolishSlate900
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) PolishSlate400 else PolishSlate500,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Reading packaging text • Extracting drug name & strength • Verifying safety warnings",
            style = MaterialTheme.typography.labelSmall,
            color = if (isDark) PolishSlate400 else PolishSlate400,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VerificationScreen(
    state: ScanUiState.Verification,
    onFieldsChanged: (String, String, String, String, String, String, String) -> Unit,
    onConfirm: () -> Unit,
    onRescan: () -> Unit,
    onOpenExisting: (com.example.data.model.MedicineEntity) -> Unit
) {
    val isDark = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Verify Extracted Information",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )
            Text(
                text = "Please check all details against the physical package before continuing.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }

        // Thumbnail Preview + Confidence Badge (GlassCard)
        item {
            GlassCard(
                level = GlassLevel.Soft,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.bitmap != null) {
                        Image(
                            bitmap = state.bitmap.asImageBitmap(),
                            contentDescription = "Scanned Package Image",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) PolishBluePrimary.copy(alpha = 0.3f) else PolishBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = PolishBluePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "AI Confidence: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                            GlassPill(
                                text = state.result.confidence,
                                variant = GlassPillVariant.Primary
                            )
                        }

                        if (state.result.uncertainFields.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Unclear fields: ${state.result.uncertainFields.joinToString()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusAlert
                            )
                        }
                    }

                    IconButton(onClick = onRescan) {
                        Icon(Icons.Default.Refresh, contentDescription = "Rescan", tint = PolishBluePrimary)
                    }
                }
            }
        }

        // Duplicate Medicine Match Banner if detected
        if (state.duplicateMatch != null) {
            item {
                GlassCard(
                    level = GlassLevel.Soft,
                    tint = SafetyAmberContainer,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = SafetyAmberOnContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Duplicate Medicine Detected",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SafetyAmberOnContainer
                            )
                        }
                        Text(
                            text = "'${state.duplicateMatch.name}' is already in your medicine list.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SafetyAmberOnContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GlassButton(
                            text = "Open Existing",
                            variant = GlassButtonVariant.TranslucentGlass,
                            height = 36.dp,
                            onClick = { onOpenExisting(state.duplicateMatch) }
                        )
                    }
                }
            }
        }

        // Editable Form Fields (GlassCard)
        item {
            GlassCard(
                level = GlassLevel.Soft,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassInput(
                        value = state.editedName,
                        onValueChange = { name ->
                            onFieldsChanged(name, state.editedBrand, state.editedStrength, state.editedForm, state.editedManufacturer, state.editedExpiry, state.editedNotes)
                        },
                        placeholder = "e.g. Paracetamol",
                        label = "Medicine / Drug Name *",
                        singleLine = true
                    )

                    GlassInput(
                        value = state.editedBrand,
                        onValueChange = { brand ->
                            onFieldsChanged(state.editedName, brand, state.editedStrength, state.editedForm, state.editedManufacturer, state.editedExpiry, state.editedNotes)
                        },
                        placeholder = "e.g. Panadol",
                        label = "Brand Name",
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassInput(
                            value = state.editedStrength,
                            onValueChange = { str ->
                                onFieldsChanged(state.editedName, state.editedBrand, str, state.editedForm, state.editedManufacturer, state.editedExpiry, state.editedNotes)
                            },
                            placeholder = "500 mg",
                            label = "Strength",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        GlassInput(
                            value = state.editedForm,
                            onValueChange = { form ->
                                onFieldsChanged(state.editedName, state.editedBrand, state.editedStrength, form, state.editedManufacturer, state.editedExpiry, state.editedNotes)
                            },
                            placeholder = "Tablet",
                            label = "Form",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassInput(
                            value = state.editedManufacturer,
                            onValueChange = { mfr ->
                                onFieldsChanged(state.editedName, state.editedBrand, state.editedStrength, state.editedForm, mfr, state.editedExpiry, state.editedNotes)
                            },
                            placeholder = "e.g. GSK",
                            label = "Manufacturer",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        GlassInput(
                            value = state.editedExpiry,
                            onValueChange = { exp ->
                                onFieldsChanged(state.editedName, state.editedBrand, state.editedStrength, state.editedForm, state.editedManufacturer, exp, state.editedNotes)
                            },
                            placeholder = "MM/YYYY",
                            label = "Expiry Date",
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    GlassInput(
                        value = state.editedNotes,
                        onValueChange = { notes ->
                            onFieldsChanged(state.editedName, state.editedBrand, state.editedStrength, state.editedForm, state.editedManufacturer, state.editedExpiry, notes)
                        },
                        placeholder = "Special instructions or warnings",
                        label = "Notes / Warnings",
                        singleLine = false
                    )
                }
            }
        }

        item {
            SafetyDisclaimerBanner(isWarning = true)
        }

        item {
            GlassButton(
                text = "Confirm & Create Reminder",
                icon = Icons.Default.CheckCircle,
                variant = GlassButtonVariant.PrimaryAccent,
                height = 54.dp,
                onClick = onConfirm
            )
        }
    }
}

@Composable
private fun ManualEntryForm(
    capturedBitmap: Bitmap?,
    name: String, onNameChange: (String) -> Unit,
    brand: String, onBrandChange: (String) -> Unit,
    strength: String, onStrengthChange: (String) -> Unit,
    form: String, onFormChange: (String) -> Unit,
    manufacturer: String, onManufacturerChange: (String) -> Unit,
    expiry: String, onExpiryChange: (String) -> Unit,
    notes: String, onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onRetakePhoto: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val commonForms = listOf("Tablet", "Capsule", "Syrup", "Drops", "Injection", "Ointment", "Other")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "Add Medicine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else PolishSlate900
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                "Enter medicine packaging details manually.",
                style = MaterialTheme.typography.bodySmall,
                color = if (isDark) PolishSlate400 else PolishSlate500
            )
        }

        if (capturedBitmap != null) {
            item {
                GlassCard(
                    level = GlassLevel.Soft,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = capturedBitmap.asImageBitmap(),
                            contentDescription = "Medicine Package",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Medicine Package",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else PolishSlate900
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "You can use the package photo to enter the information manually.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) PolishSlate400 else PolishSlate500
                            )
                        }
                        IconButton(onClick = onRetakePhoto) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Retake Photo", tint = PolishBluePrimary)
                        }
                    }
                }
            }
        }

        item {
            GlassInput(
                value = name,
                onValueChange = onNameChange,
                label = "Medicine Name *",
                placeholder = "e.g. Paracetamol, Amoxicillin",
                singleLine = true
            )
        }

        item {
            GlassInput(
                value = brand,
                onValueChange = onBrandChange,
                label = "Brand Name",
                placeholder = "e.g. Tylenol, Panadol",
                singleLine = true
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassInput(
                    value = strength,
                    onValueChange = onStrengthChange,
                    label = "Strength",
                    placeholder = "e.g. 500 mg",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                GlassInput(
                    value = form,
                    onValueChange = onFormChange,
                    label = "Form",
                    placeholder = "Tablet",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            Column {
                Text(
                    "Select Form:",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDark) PolishSlate400 else PolishSlate500
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(commonForms) { option ->
                        val selected = form.equals(option, ignoreCase = true)
                        GlassChip(
                            text = option,
                            selected = selected,
                            onClick = { onFormChange(option) }
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassInput(
                    value = manufacturer,
                    onValueChange = onManufacturerChange,
                    label = "Manufacturer",
                    placeholder = "e.g. Pfizer, GSK",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                GlassInput(
                    value = expiry,
                    onValueChange = onExpiryChange,
                    label = "Expiry Date",
                    placeholder = "MM/YYYY",
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        item {
            GlassInput(
                value = notes,
                onValueChange = onNotesChange,
                label = "Notes / Dosage Instructions",
                placeholder = "Special instructions or warnings",
                singleLine = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassButton(
                    text = "Cancel",
                    variant = GlassButtonVariant.TranslucentGlass,
                    modifier = Modifier.weight(1f),
                    onClick = onCancel
                )
                GlassButton(
                    text = "Save Medicine",
                    variant = GlassButtonVariant.PrimaryAccent,
                    enabled = name.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    onClick = onSave
                )
            }
        }
    }
}

@Composable
private fun ScanErrorView(
    title: String,
    errorMessage: String,
    onRetry: () -> Unit,
    onManualEntry: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusAlert, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else PolishSlate900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDark) PolishSlate400 else PolishSlate500,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        GlassButton(
            text = "Try Again",
            icon = Icons.Default.Refresh,
            variant = GlassButtonVariant.PrimaryAccent,
            height = 52.dp,
            onClick = onRetry
        )
        Spacer(modifier = Modifier.height(12.dp))
        GlassButton(
            text = "Enter Manually",
            variant = GlassButtonVariant.TranslucentGlass,
            height = 52.dp,
            onClick = onManualEntry
        )
    }
}
