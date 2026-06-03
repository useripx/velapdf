package com.njagakneai.velapdf.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.model.DocumentType
import com.njagakneai.velapdf.data.model.MergeableDocument
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.utils.FileUriHelper
import com.njagakneai.velapdf.utils.PdfMergerEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import android.os.Environment
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.model.HistoryEntity
import com.njagakneai.velapdf.data.preferences.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class MergePdfViewModel @Inject constructor(
    private val historyDao: HistoryDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val documents = mutableStateListOf<MergeableDocument>()

    private val _mergeState = MutableStateFlow<PdfGenerationState>(PdfGenerationState.Idle)
    val mergeState: StateFlow<PdfGenerationState> = _mergeState.asStateFlow()

    private val _isLoadingFiles = MutableStateFlow(false)
    val isLoadingFiles: StateFlow<Boolean> = _isLoadingFiles.asStateFlow()

    val totalPages: Int
        get() = documents.sumOf { it.pageCount }

    val totalFiles: Int
        get() = documents.size

    /**
     * Validates current document list and returns a result.
     */
    fun validate(): PdfMergerEngine.ValidationResult {
        return PdfMergerEngine.validate(documents.toList())
    }

    /**
     * Adds documents from selected URIs.
     * Auto-detects type (PDF or Image) and reads page count for PDFs.
     */
    fun addDocuments(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return

        val remainingSlots = PdfMergerEngine.MAX_FILES - documents.size
        if (remainingSlots <= 0) return

        _isLoadingFiles.value = true

        viewModelScope.launch {
            val newDocs = mutableListOf<MergeableDocument>()

            withContext(Dispatchers.IO) {
                uris.take(remainingSlots).forEach { uri ->
                    try {
                        val fileName = FileUriHelper.getFileName(context, uri)
                        val mimeType = context.contentResolver.getType(uri) ?: ""
                        val cachedUri = FileUriHelper.copyUriToCache(context, uri) ?: return@forEach

                        val fileSize = getFileSize(context, uri)

                        val type = when {
                            mimeType == "application/pdf" || fileName.lowercase().endsWith(".pdf") -> DocumentType.PDF
                            mimeType.startsWith("image/") -> DocumentType.IMAGE
                            else -> return@forEach // Skip unsupported types
                        }

                        val doc = MergeableDocument(
                            originalUri = uri,
                            cachedUri = cachedUri,
                            fileName = fileName,
                            pageCount = 1,
                            fileSizeBytes = fileSize,
                            type = type,
                            orderIndex = documents.size + newDocs.size
                        )

                        // Get actual page count for PDF files
                        val finalDoc = if (type == DocumentType.PDF) {
                            val pageCount = PdfMergerEngine.getPdfPageCount(context, doc)
                            doc.copy(pageCount = pageCount)
                        } else {
                            doc
                        }

                        newDocs.add(finalDoc)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            documents.addAll(newDocs)
            _isLoadingFiles.value = false
        }
    }

    /**
     * Removes a document at the given index.
     */
    fun removeDocument(index: Int) {
        if (index in documents.indices) {
            documents.removeAt(index)
        }
    }

    /**
     * Removes all documents.
     */
    fun clearAll() {
        documents.clear()
    }

    /**
     * Swaps documents at positions from and to (for reordering).
     */
    fun moveDocument(from: Int, to: Int) {
        if (from in documents.indices && to in documents.indices && from != to) {
            val item = documents.removeAt(from)
            documents.add(to, item)
        }
    }

    /**
     * Starts the merge process.
     */
    fun startMerge(context: Context, outputFileName: String, saveAsUri: Uri?) {
        val validation = validate()
        if (!validation.isValid) {
            _mergeState.value = PdfGenerationState.Error(
                IllegalArgumentException(validation.errorMessage)
            )
            return
        }

        _mergeState.value = PdfGenerationState.Loading(0)

        viewModelScope.launch {
            try {
                // Determine output destination
                val tempFile = File(context.cacheDir, "${outputFileName}_temp.pdf")
                
                val quality = preferencesManager.compressionQuality.first()
                
                PdfMergerEngine.mergeDocuments(
                    context = context,
                    documents = documents.toList(),
                    outputFile = tempFile,
                    compressionQuality = quality,
                    onProgress = { progress ->
                        _mergeState.value = PdfGenerationState.Loading(progress)
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

                _mergeState.value = PdfGenerationState.Success(finalUri)
            } catch (e: Exception) {
                e.printStackTrace()
                _mergeState.value = PdfGenerationState.Error(e)
            }
        }
    }

    /**
     * Resets the merge state back to Idle.
     */
    fun resetState() {
        _mergeState.value = PdfGenerationState.Idle
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) it.getLong(sizeIndex) else 0L
                } else 0L
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
