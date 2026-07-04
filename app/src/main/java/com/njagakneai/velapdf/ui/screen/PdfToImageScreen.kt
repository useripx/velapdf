package com.njagakneai.velapdf.ui.screen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.data.model.ImageFormat
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.ui.components.NotificationData
import com.njagakneai.velapdf.ui.components.NotificationToast
import com.njagakneai.velapdf.ui.components.NotificationType
import com.njagakneai.velapdf.ui.viewmodel.PdfToImageViewModel
import com.njagakneai.velapdf.utils.FileUriHelper
import com.njagakneai.velapdf.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToImageScreen(
    onNavigateBack: () -> Unit,
    onConvertSuccess: ((String) -> Unit)? = null,
    viewModel: PdfToImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val conversionState by viewModel.conversionState.collectAsState()
    var toastNotification by remember { mutableStateOf<NotificationData?>(null) }
    
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf("") }
    var pageCount by remember { mutableStateOf(0) }
    var outputFileName by remember { mutableStateOf("") }
    
    var isSaveAsMode by remember { mutableStateOf(false) }
    var isZipMode by remember { mutableStateOf(true) }
    
    var selectedFormat by remember { mutableStateOf(ImageFormat.JPG) }
    var expandedFormatDropdown by remember { mutableStateOf(false) }

    val isConverting = conversionState is PdfGenerationState.Loading
    val convertProgress = (conversionState as? PdfGenerationState.Loading)?.progress ?: 0

    val animatedProgress by animateFloatAsState(
        targetValue = convertProgress.toFloat() / 100f,
        animationSpec = tween(durationMillis = 300),
        label = "convert_progress"
    )

    LaunchedEffect(isZipMode, selectedFormat, selectedPdfName) {
        if (selectedPdfName.isNotEmpty()) {
            val baseName = selectedPdfName.substringBeforeLast(".")
            outputFileName = if (isZipMode) {
                "${baseName}_Images.zip"
            } else {
                if (pageCount > 1) {
                    "${baseName}_Images" // Folder name hint
                } else {
                    "${baseName}.${selectedFormat.extension}"
                }
            }
        }
    }

    // File picker for PDF
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedPdfUri = uri
            selectedPdfName = FileUriHelper.getFileName(context, uri)
            pageCount = viewModel.getPageCount(uri)
            isZipMode = pageCount > 1 // Smart detection default
        }
    }

    // Save As Launcher
    val saveAsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
            val uri = result.data!!.data!!
            viewModel.convertPdfToImage(
                inputUri = selectedPdfUri!!,
                inputFileName = selectedPdfName,
                format = selectedFormat,
                isZipMode = isZipMode,
                outputFileName = outputFileName,
                customOutputDir = uri
            )
        }
    }

    // Handle conversion state changes
    LaunchedEffect(conversionState) {
        when (val state = conversionState) {
            is PdfGenerationState.Success -> {
                NotificationHelper.showPdfCompleteNotification(
                    context,
                    outputFileName,
                    state.uri
                )
                toastNotification = NotificationData(
                    "Konversi berhasil!",
                    NotificationType.Success
                )
                onConvertSuccess?.invoke(Uri.encode(state.uri.toString()))
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
                        // Progress bar during conversion
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
                        }

                        Button(
                            onClick = {
                                if (selectedPdfUri == null || isConverting) return@Button
                                if (isSaveAsMode) {
                                    if (!isZipMode && pageCount > 1) {
                                        saveAsLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                                    } else {
                                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                                            addCategory(Intent.CATEGORY_OPENABLE)
                                            type = if (isZipMode) "application/zip" else "image/${selectedFormat.extension}"
                                            putExtra(Intent.EXTRA_TITLE, outputFileName)
                                        }
                                        saveAsLauncher.launch(intent)
                                    }
                                } else {
                                    viewModel.convertPdfToImage(
                                        inputUri = selectedPdfUri!!,
                                        inputFileName = selectedPdfName,
                                        format = selectedFormat,
                                        isZipMode = isZipMode,
                                        outputFileName = outputFileName
                                    )
                                }
                            },
                            enabled = selectedPdfUri != null && !isConverting,
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
                                    text = "Mengonversi… $convertProgress%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isZipMode) "Konversi ke ZIP" else "Konversi ke Gambar",
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
                    text = "PDF to Image",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Convert pages of a PDF document into high quality JPG, PNG, or WebP images.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Select PDF
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    if (selectedPdfUri != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF File",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedPdfName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$pageCount Halaman | Siap Dikonversi",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isConverting
                        ) {
                            Text("Ganti File PDF")
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Output Format Selection
                        Text(
                            text = "Format Gambar Hasil Konversi",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedFormatDropdown,
                            onExpandedChange = { if (!isConverting) expandedFormatDropdown = !expandedFormatDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedFormat.displayName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFormatDropdown) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedFormatDropdown,
                                onDismissRequest = { expandedFormatDropdown = false }
                            ) {
                                ImageFormat.entries.forEach { format ->
                                    DropdownMenuItem(
                                        text = { Text(text = format.displayName) },
                                        onClick = {
                                            selectedFormat = format
                                            expandedFormatDropdown = false
                                        },
                                        trailingIcon = {
                                            if (format == selectedFormat) {
                                                Icon(Icons.Default.Check, null)
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Simpan sebagai arsip ZIP", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = isZipMode, 
                                onCheckedChange = { isZipMode = it },
                                enabled = !isConverting
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Output Name
                        OutlinedTextField(
                            value = outputFileName,
                            onValueChange = { outputFileName = it },
                            label = { Text(if (isZipMode) "Nama File ZIP" else if (pageCount > 1) "Nama Folder Output" else "Nama File Gambar") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !isConverting
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pilih lokasi manual (Save As)", style = MaterialTheme.typography.bodyMedium)
                            Switch(
                                checked = isSaveAsMode, 
                                onCheckedChange = { isSaveAsMode = it },
                                enabled = !isConverting
                            )
                        }
                        
                    } else {
                        // Empty State
                        Button(
                            onClick = { pdfPickerLauncher.launch(arrayOf("application/pdf")) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = CircleShape
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah File")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pilih File PDF",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Notification Toast overlay
        NotificationToast(
            notification = toastNotification,
            onDismiss = { toastNotification = null },
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
