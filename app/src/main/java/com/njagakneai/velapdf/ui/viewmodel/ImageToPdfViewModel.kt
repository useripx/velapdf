package com.njagakneai.velapdf.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.model.HistoryEntity
import com.njagakneai.velapdf.data.model.SelectedImage
import com.njagakneai.velapdf.data.preferences.PreferencesManager
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.utils.PdfGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageToPdfViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _conversionState = MutableStateFlow<PdfGenerationState>(PdfGenerationState.Idle)
    val conversionState: StateFlow<PdfGenerationState> = _conversionState.asStateFlow()

    fun generatePdf(
        context: Context,
        images: List<SelectedImage>,
        outputFileName: String,
        saveAsUri: Uri?
    ) {
        _conversionState.value = PdfGenerationState.Loading(0)
        
        viewModelScope.launch {
            try {
                // Determine output destination
                val tempFile = File(context.cacheDir, "${outputFileName}_temp.pdf")
                
                val quality = preferencesManager.compressionQuality.first()
                
                PdfGenerator.generatePdfFromImages(
                    context = context,
                    images = images,
                    outputFile = tempFile,
                    compressionQuality = quality,
                    onProgress = { progress ->
                        _conversionState.value = PdfGenerationState.Loading(progress)
                    }
                )

                val finalUri: Uri
                if (saveAsUri != null) {
                    // Copy temp file to chosen Save As URI
                    context.contentResolver.openOutputStream(saveAsUri)?.use { outStream ->
                        tempFile.inputStream().use { inStream ->
                            inStream.copyTo(outStream)
                        }
                    }
                    finalUri = saveAsUri
                } else {
                    // Save to Documents/VelaPDF by default
                    val documentsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "VelaPDF")
                    if (!documentsDir.exists()) {
                        documentsDir.mkdirs()
                    }
                    val finalFile = File(documentsDir, "$outputFileName.pdf")
                    tempFile.copyTo(finalFile, overwrite = true)
                    
                    finalUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        finalFile
                    )
                }
                
                // Cleanup temp
                if (tempFile.exists()) tempFile.delete()

                // Insert to history
                historyDao.insertHistory(
                    HistoryEntity(
                        fileName = "$outputFileName.pdf",
                        filePath = finalUri.toString(),
                        fileSize = tempFile.length(),
                        timestamp = System.currentTimeMillis()
                    )
                )

                _conversionState.value = PdfGenerationState.Success(finalUri)
            } catch (e: Exception) {
                e.printStackTrace()
                _conversionState.value = PdfGenerationState.Error(e)
            }
        }
    }
    
    fun resetState() {
        _conversionState.value = PdfGenerationState.Idle
    }
}
