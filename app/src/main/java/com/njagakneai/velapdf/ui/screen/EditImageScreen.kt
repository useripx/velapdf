package com.njagakneai.velapdf.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.njagakneai.velapdf.data.model.EditOutputFormat
import com.njagakneai.velapdf.ui.components.FormatSelectorChips
import com.njagakneai.velapdf.ui.viewmodel.EditImageViewModel
import com.njagakneai.velapdf.ui.viewmodel.EditSaveState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditImageScreen(
    imageUri: Uri,
    onNavigateBack: () -> Unit,
    onSaveSuccess: (originalUri: Uri, newUri: Uri) -> Unit,
    viewModel: EditImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    LaunchedEffect(imageUri) {
        viewModel.initialize(imageUri)
    }

    val currentUri by viewModel.currentUri.collectAsState()
    val selectedFormat by viewModel.selectedFormat.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    var fileName by remember { mutableStateOf("Edited_${System.currentTimeMillis()}") }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            scanResult?.pages?.firstOrNull()?.imageUri?.let { newUri ->
                viewModel.updateImage(newUri)
            }
        }
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is EditSaveState.Success -> {
                Toast.makeText(context, "Disimpan di: ${state.path}", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                onSaveSuccess(imageUri, state.uri)
            }
            is EditSaveState.Error -> {
                Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Foto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Image Preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
            ) {
                if (currentUri != null) {
                    AsyncImage(
                        model = currentUri,
                        contentDescription = "Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Button(
                onClick = {
                    val options = GmsDocumentScannerOptions.Builder()
                        .setGalleryImportAllowed(true)
                        .setPageLimit(1)
                        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
                        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_BASE_WITH_FILTER)
                        .build()

                    val scanner = GmsDocumentScanning.getClient(options)
                    val activity = context as? android.app.Activity
                    if (activity != null) {
                        scanner.getStartScanIntent(activity)
                            .addOnSuccessListener { intentSender ->
                                scannerLauncher.launch(
                                    IntentSenderRequest.Builder(intentSender).build()
                                )
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Gagal memulai ML Kit: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pindai Ulang Dokumen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Format Selection
            Text("Format Output:", fontWeight = FontWeight.Medium)
            FormatSelectorChips(
                selectedFormat = selectedFormat,
                onFormatSelected = { viewModel.setFormat(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = fileName,
                onValueChange = { fileName = it },
                label = { Text("Nama File") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveImage(context, fileName, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = saveState !is EditSaveState.Saving
            ) {
                if (saveState is EditSaveState.Saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan")
                }
            }
        }
    }
}
