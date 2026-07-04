package com.njagakneai.velapdf.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.widget.Toast
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.njagakneai.velapdf.data.model.SelectedImage
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.data.repository.PdfRepository
import com.njagakneai.velapdf.ui.components.NotificationData
import com.njagakneai.velapdf.ui.components.NotificationToast
import com.njagakneai.velapdf.ui.components.NotificationType
import com.njagakneai.velapdf.ui.components.SortableImageGrid
import com.njagakneai.velapdf.utils.FileUriHelper
import com.njagakneai.velapdf.utils.NotificationHelper
import com.njagakneai.velapdf.ui.viewmodel.ImageToPdfViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.data.model.CompressionLevel
import com.njagakneai.velapdf.ui.components.CompressionDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageToPdfScreen(
    onNavigateBack: () -> Unit,
    onConversionSuccess: ((String) -> Unit)? = null,
    viewModel: ImageToPdfViewModel = hiltViewModel()
) {
    var selectedImages by remember { mutableStateOf(emptyList<SelectedImage>()) }
    var outputFileName by remember { mutableStateOf("VelaPDF_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}") }
    var isSaveAsMode by remember { mutableStateOf(false) }
    var toastNotification by remember { mutableStateOf<NotificationData?>(null) }
    
    var showCompressionDialog by remember { mutableStateOf(false) }
    var pendingCompressionLevel by remember { mutableStateOf(CompressionLevel.BIASA) }

    val conversionState by viewModel.conversionState.collectAsState()
    val isConverting = conversionState is PdfGenerationState.Loading
    val conversionProgress = (conversionState as? PdfGenerationState.Loading)?.progress ?: 0

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val maxSelection = 50
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelection)
    ) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val newSelectedImages = mutableListOf<SelectedImage>()
                withContext(Dispatchers.IO) {
                    uris.take(maxSelection).forEachIndexed { index, uri ->
                        val cachedUri = FileUriHelper.copyUriToCache(context, uri)
                        val fileName = FileUriHelper.getFileName(context, uri)
                        if (cachedUri != null) {
                            newSelectedImages.add(
                                SelectedImage(
                                    originalUri = uri,
                                    cachedUri = cachedUri,
                                    fileName = fileName,
                                    orderIndex = index
                                )
                            )
                        }
                    }
                }
                selectedImages = newSelectedImages
            }
        }
    }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanningResult?.pages?.let { pages ->
                coroutineScope.launch {
                    val newSelectedImages = selectedImages.toMutableList()
                    withContext(Dispatchers.IO) {
                        pages.forEachIndexed { index, page ->
                            val uri = page.imageUri
                            val cachedUri = FileUriHelper.copyUriToCache(context, uri)
                            val fileName = "Scan_${System.currentTimeMillis()}_$index.jpg"
                            if (cachedUri != null) {
                                newSelectedImages.add(
                                    SelectedImage(
                                        originalUri = uri,
                                        cachedUri = cachedUri,
                                        fileName = fileName,
                                        orderIndex = newSelectedImages.size
                                    )
                                )
                            }
                        }
                    }
                    selectedImages = newSelectedImages
                }
            }
        }
    }

    // Save As Launcher
    val saveAsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            viewModel.generatePdf(context, selectedImages, outputFileName, uri, pendingCompressionLevel)
        }
    }

    LaunchedEffect(conversionState) {
        when (val state = conversionState) {
            is PdfGenerationState.Success -> {
                NotificationHelper.showPdfCompleteNotification(
                    context,
                    "$outputFileName.pdf",
                    state.uri
                )
                toastNotification = NotificationData(
                    "PDF berhasil dibuat!",
                    NotificationType.Success
                )
                onConversionSuccess?.invoke(Uri.encode(state.uri.toString()))
                viewModel.resetState()
            }
            is PdfGenerationState.Error -> {
                toastNotification = NotificationData(
                    state.exception.message ?: "Gagal mengonversi PDF",
                    NotificationType.Error
                )
                viewModel.resetState()
            }
            else -> {}
        }
    }

    // Animated progress for smooth visual feedback
    val animatedProgress by animateFloatAsState(
        targetValue = conversionProgress.toFloat() / 100f,
        animationSpec = tween(durationMillis = 300),
        label = "conversion_progress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "VelaPDF",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, enabled = !isConverting) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO History */ }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                    ),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .navigationBarsPadding()
                    ) {
                        // Progress bar visible during conversion
                        if (isConverting) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        } else if (selectedImages.isNotEmpty()) {
                            OutlinedTextField(
                                value = outputFileName,
                                onValueChange = { outputFileName = it },
                                label = { Text("Nama File Output") },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                singleLine = true
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pilih lokasi manual (Save As)", style = MaterialTheme.typography.bodyMedium)
                                Switch(checked = isSaveAsMode, onCheckedChange = { isSaveAsMode = it })
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedImages.isEmpty() || isConverting) return@Button
                                showCompressionDialog = true
                            },
                            enabled = selectedImages.isNotEmpty() && !isConverting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                )
                            ),
                            shape = CircleShape
                        ) {
                            if (isConverting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Mengonversi… $conversionProgress%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Text(
                                    text = "Convert to PDF",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // Hero
                Text(
                    text = "Image to PDF",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Convert your photos, screenshots, and scans into professional PDF documents in seconds.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Upload and Camera Buttons area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        enabled = !isConverting,
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Upload Image")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload Image",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            val options = GmsDocumentScannerOptions.Builder()
                                .setGalleryImportAllowed(false)
                                .setPageLimit(50)
                                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                                .build()
                            
                            val scanner = GmsDocumentScanning.getClient(options)
                            context.findActivity()?.let { activity ->
                                scanner.getStartScanIntent(activity)
                                    .addOnSuccessListener { intentSender ->
                                        scannerLauncher.launch(
                                            IntentSenderRequest.Builder(intentSender).build()
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Gagal memulai kamera cerdas: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } ?: run {
                                Toast.makeText(context, "Konteks aplikasi tidak valid", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = !isConverting,
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Kamera")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kamera",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Preview Area or Empty State
                if (selectedImages.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SELECTED PREVIEW",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Remove all",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.clickable(enabled = !isConverting) {
                                selectedImages = emptyList()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        SortableImageGrid(
                            selectedImages = selectedImages,
                            onImagesUpdated = { newImages -> selectedImages = newImages }
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Empty",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No image selected for conversion",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Notification Toast overlay — pinned to top of screen
        NotificationToast(
            notification = toastNotification,
            onDismiss = { toastNotification = null },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (showCompressionDialog) {
            CompressionDialog(
                onDismiss = { showCompressionDialog = false },
                onConfirm = { level ->
                    showCompressionDialog = false
                    pendingCompressionLevel = level
                    if (isSaveAsMode) {
                        saveAsLauncher.launch(outputFileName)
                    } else {
                        viewModel.generatePdf(context, selectedImages, outputFileName, null, level)
                    }
                }
            )
        }
    }
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
