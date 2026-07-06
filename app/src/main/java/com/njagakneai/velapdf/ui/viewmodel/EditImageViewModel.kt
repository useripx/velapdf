package com.njagakneai.velapdf.ui.viewmodel

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.model.EditOutputFormat
import com.njagakneai.velapdf.data.model.HistoryEntity
import com.njagakneai.velapdf.utils.BitmapProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class EditSaveState {
    object Idle : EditSaveState()
    object Saving : EditSaveState()
    data class Success(val uri: Uri, val path: String) : EditSaveState()
    data class Error(val message: String) : EditSaveState()
}

@HiltViewModel
class EditImageViewModel @Inject constructor(
    private val historyDao: HistoryDao
) : ViewModel() {

    private val _originalUri = MutableStateFlow<Uri?>(null)
    val originalUri: StateFlow<Uri?> = _originalUri.asStateFlow()

    private val _currentUri = MutableStateFlow<Uri?>(null)
    val currentUri: StateFlow<Uri?> = _currentUri.asStateFlow()

    private val _selectedFormat = MutableStateFlow(EditOutputFormat.JPG)
    val selectedFormat: StateFlow<EditOutputFormat> = _selectedFormat.asStateFlow()

    private val _saveState = MutableStateFlow<EditSaveState>(EditSaveState.Idle)
    val saveState: StateFlow<EditSaveState> = _saveState.asStateFlow()

    fun initialize(uri: Uri) {
        if (_originalUri.value == null) {
            _originalUri.value = uri
            _currentUri.value = uri
        }
    }

    fun updateImage(newUri: Uri) {
        _currentUri.value = newUri
    }

    fun setFormat(format: EditOutputFormat) {
        _selectedFormat.value = format
    }

    fun saveImage(context: Context, fileName: String, saveAsUri: Uri?) {
        val uriToSave = _currentUri.value ?: return
        _saveState.value = EditSaveState.Saving

        viewModelScope.launch {
            try {
                val format = _selectedFormat.value
                val inputStream = context.contentResolver.openInputStream(uriToSave)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    _saveState.value = EditSaveState.Error("Gagal membaca gambar")
                    return@launch
                }

                val finalUri: Uri?
                val fileExtension = if (format == EditOutputFormat.PDF) "pdf" else format.extension

                if (saveAsUri != null) {
                    // Save to user chosen location
                    val tempUri = if (format == EditOutputFormat.PDF) {
                        BitmapProcessor.saveBitmapAsPdf(context, bitmap, "${fileName}_temp")
                    } else {
                        BitmapProcessor.saveBitmapAs(context, bitmap, format, "${fileName}_temp")
                    }

                    if (tempUri != null) {
                        context.contentResolver.openOutputStream(saveAsUri)?.use { outStream ->
                            context.contentResolver.openInputStream(tempUri)?.use { inStream ->
                                inStream.copyTo(outStream)
                            }
                        }
                        // Delete temp
                        val tempFile = File(tempUri.path ?: "")
                        if (tempFile.exists()) tempFile.delete()
                    }
                    finalUri = saveAsUri
                    bitmap.recycle()
                } else {
                    // Save to Documents/VelaPDF
                    val documentsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "VelaPDF")
                    if (!documentsDir.exists()) {
                        documentsDir.mkdirs()
                    }

                    val finalFile = File(documentsDir, "$fileName.$fileExtension")
                    
                    if (format == EditOutputFormat.PDF) {
                        val tempUri = BitmapProcessor.saveBitmapAsPdf(context, bitmap, fileName)
                        if (tempUri != null) {
                            val tempFile = File(tempUri.path ?: "")
                            tempFile.copyTo(finalFile, overwrite = true)
                            if (tempFile.exists()) tempFile.delete()
                        }
                    } else {
                        val tempUri = BitmapProcessor.saveBitmapAs(context, bitmap, format, fileName)
                        if (tempUri != null) {
                            val tempFile = File(tempUri.path ?: "")
                            tempFile.copyTo(finalFile, overwrite = true)
                            if (tempFile.exists()) tempFile.delete()
                        }
                    }
                    bitmap.recycle()

                    finalUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        finalFile
                    )

                    // Insert to history
                    historyDao.insertHistory(
                        HistoryEntity(
                            fileName = "$fileName.$fileExtension",
                            filePath = finalUri.toString(),
                            fileSize = finalFile.length(),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }

                if (finalUri != null) {
                    _saveState.value = EditSaveState.Success(finalUri, "$fileName.$fileExtension")
                } else {
                    _saveState.value = EditSaveState.Error("Gagal menyimpan file")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _saveState.value = EditSaveState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _saveState.value = EditSaveState.Idle
    }
}
