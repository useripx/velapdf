package com.njagakneai.velapdf.ui.screen

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.automirrored.filled.MergeType
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.ui.components.NotificationData
import com.njagakneai.velapdf.ui.components.NotificationToast
import com.njagakneai.velapdf.ui.components.NotificationType
import com.njagakneai.velapdf.ui.components.SortableDocumentList
import com.njagakneai.velapdf.ui.viewmodel.MergePdfViewModel
import com.njagakneai.velapdf.utils.NotificationHelper
import com.njagakneai.velapdf.utils.PdfMergerEngine
import java.text.SimpleDateFormat
import java.util.*

import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.data.model.CompressionLevel
import com.njagakneai.velapdf.ui.components.CompressionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MergePdfScreen(
    onNavigateBack: () -> Unit,
    onMergeSuccess: ((String) -> Unit)? = null,
    viewModel: MergePdfViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mergeState by viewModel.mergeState.collectAsState()
    val isLoadingFiles by viewModel.isLoadingFiles.collectAsState()
    var toastNotification by remember { mutableStateOf<NotificationData?>(null) }
    var outputFileName by remember { mutableStateOf("VelaPDF_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}") }
    var isSaveAsMode by remember { mutableStateOf(false) }

    var showCompressionDialog by remember { mutableStateOf(false) }
    var pendingCompressionLevel by remember { mutableStateOf(CompressionLevel.BIASA) }

    val isMerging = mergeState is PdfGenerationState.Loading
    val mergeProgress = (mergeState as? PdfGenerationState.Loading)?.progress ?: 0

    val animatedProgress by animateFloatAsState(
        targetValue = mergeProgress.toFloat() / 100f,
        animationSpec = tween(durationMillis = 300),
        label = "merge_progress"
    )

    // File picker for PDFs and Images
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.addDocuments(context, uris)
        }
    }

    // Save As Launcher
    val saveAsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            viewModel.startMerge(context, outputFileName, uri, pendingCompressionLevel)
        }
    }

    // Handle merge state changes
    LaunchedEffect(mergeState) {
        when (val state = mergeState) {
            is PdfGenerationState.Success -> {
                NotificationHelper.showPdfCompleteNotification(
                    context,
                    "MergePDF_${System.currentTimeMillis()}.pdf",
                    state.uri
                )
                toastNotification = NotificationData(
                    "PDF berhasil digabungkan!",
                    NotificationType.Success
                )
                onMergeSuccess?.invoke(Uri.encode(state.uri.toString()))
            }

            is PdfGenerationState.Error -> {
                toastNotification = NotificationData(
                    state.exception.message ?: "Gagal menggabungkan PDF",
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
                        IconButton(onClick = onNavigateBack, enabled = !isMerging) {
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
                        // Progress bar during merge
                        if (isMerging) {
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
                        } else if (viewModel.documents.isNotEmpty()) {
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
                                if (viewModel.documents.isEmpty() || isMerging) return@Button
                                val validation = viewModel.validate()
                                if (!validation.isValid) {
                                    toastNotification = NotificationData(
                                        validation.errorMessage ?: "Validasi gagal",
                                        NotificationType.Error
                                    )
                                    return@Button
                                }
                                showCompressionDialog = true
                            },
                            enabled = viewModel.documents.size >= 2 && !isMerging,
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
                            if (isMerging) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Menggabungkan… $mergeProgress%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.MergeType,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Merge PDF",
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
                    text = "Merge PDF",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Combine multiple PDF files and images into a single PDF document. Arrange the order as needed.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Upload Button area
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            documentPickerLauncher.launch(
                                arrayOf("application/pdf", "image/jpeg", "image/png", "image/webp")
                            )
                        },
                        enabled = !isMerging && viewModel.documents.size < PdfMergerEngine.MAX_FILES,
                        modifier = Modifier.height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah File")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tambah File PDF / Gambar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Loading indicator for file loading
                if (isLoadingFiles) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                // Document list or empty state
                if (viewModel.documents.isNotEmpty()) {
                    // Stats bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "URUTAN DOKUMEN",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${viewModel.totalFiles} file · ${viewModel.totalPages} halaman",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Text(
                            text = "Hapus semua",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.clickable(enabled = !isMerging) {
                                viewModel.clearAll()
                            }
                        )
                    }

                    // Capacity indicator
                    val totalPages = viewModel.totalPages
                    val pageRatio = totalPages.toFloat() / PdfMergerEngine.MAX_TOTAL_PAGES
                    LinearProgressIndicator(
                        progress = { pageRatio.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (pageRatio > 0.9f) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = "$totalPages / ${PdfMergerEngine.MAX_TOTAL_PAGES} halaman",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (pageRatio > 0.9f) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )

                    // Document list
                    Box(modifier = Modifier.weight(1f)) {
                        SortableDocumentList(
                            documents = viewModel.documents,
                            onMoveUp = { index ->
                                if (index > 0) viewModel.moveDocument(index, index - 1)
                            },
                            onMoveDown = { index ->
                                if (index < viewModel.documents.size - 1) viewModel.moveDocument(index, index + 1)
                            },
                            onRemove = { index -> viewModel.removeDocument(index) }
                        )
                    }
                } else {
                    // Empty State
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
                                imageVector = Icons.AutoMirrored.Filled.MergeType,
                                contentDescription = "Empty",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Belum ada file yang dipilih",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tambahkan minimal 2 file PDF atau gambar untuk mulai menggabungkan",
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
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

        if (showCompressionDialog) {
            CompressionDialog(
                onDismiss = { showCompressionDialog = false },
                onConfirm = { level ->
                    showCompressionDialog = false
                    pendingCompressionLevel = level
                    if (isSaveAsMode) {
                        saveAsLauncher.launch(outputFileName)
                    } else {
                        viewModel.startMerge(context, outputFileName, null, level)
                    }
                }
            )
        }
    }
}
