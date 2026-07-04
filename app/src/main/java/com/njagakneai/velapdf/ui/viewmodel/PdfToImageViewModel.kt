package com.njagakneai.velapdf.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.njagakneai.velapdf.data.database.HistoryDao
import com.njagakneai.velapdf.data.model.HistoryEntity
import com.njagakneai.velapdf.data.model.ImageFormat
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.utils.FileUriHelper
import com.njagakneai.velapdf.utils.PdfToImageEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PdfToImageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val historyDao: HistoryDao
) : ViewModel() {

    private val _conversionState = MutableStateFlow<PdfGenerationState>(PdfGenerationState.Idle)
    val conversionState: StateFlow<PdfGenerationState> = _conversionState.asStateFlow()

    private val engine = PdfToImageEngine(context)

    fun resetState() {
        _conversionState.value = PdfGenerationState.Idle
    }

    fun getPageCount(inputUri: Uri): Int {
        return try {
            context.contentResolver.openFileDescriptor(inputUri, "r")?.use {
                android.graphics.pdf.PdfRenderer(it).pageCount
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun convertPdfToImage(
        inputUri: Uri,
        inputFileName: String,
        format: ImageFormat,
        isZipMode: Boolean,
        outputFileName: String,
        customOutputDir: Uri? = null // For Save As (File or Tree URI)
    ) {
        _conversionState.value = PdfGenerationState.Loading(0)

        viewModelScope.launch {
            try {
                val pageCount = getPageCount(inputUri)
                if (pageCount == 0) throw Exception("Dokumen PDF kosong atau gagal dibaca.")
                
                var finalOutputUri: Uri? = null
                var finalOutputName = ""
                var finalSize = 0L

                if (isZipMode) {
                    val safeFileName = outputFileName.ifBlank {
                        "${inputFileName.substringBeforeLast(".")}_Images.zip"
                    }.let { if (!it.endsWith(".zip", true)) "$it.zip" else it }

                    val cacheDir = File(context.cacheDir, "pdf_to_image_exports")
                    if (!cacheDir.exists()) cacheDir.mkdirs()
                    val outputFile = File(cacheDir, safeFileName)

                    engine.convertPdfToImages(
                        inputUri = inputUri,
                        format = format,
                        isZipMode = true,
                        outputZipFile = outputFile,
                        onProgress = { _conversionState.value = PdfGenerationState.Loading(it) }
                    ).onSuccess {
                        finalSize = outputFile.length()
                        val (finalPath, finalName) = if (customOutputDir != null) {
                            context.contentResolver.openOutputStream(customOutputDir)?.use { out ->
                                outputFile.inputStream().use { it.copyTo(out) }
                            }
                            customOutputDir.toString() to FileUriHelper.getFileName(context, customOutputDir)
                        } else {
                            val publicDocsDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "VelaPDF")
                            if (!publicDocsDir.exists()) publicDocsDir.mkdirs()
                            var safeFinalFile = File(publicDocsDir, safeFileName)
                            var index = 1
                            while (safeFinalFile.exists()) {
                                safeFinalFile = File(publicDocsDir, "${safeFileName.substringBeforeLast(".zip")}_$index.zip")
                                index++
                            }
                            outputFile.copyTo(safeFinalFile, overwrite = true)
                            Uri.fromFile(safeFinalFile).toString() to safeFinalFile.name
                        }
                        outputFile.delete()
                        finalOutputUri = Uri.parse(finalPath)
                        finalOutputName = finalName
                    }.onFailure { throw it }
                } else {
                    // Non-ZIP Mode
                    val publicDocsDir = File(android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOCUMENTS), "VelaPDF")
                    if (!publicDocsDir.exists()) publicDocsDir.mkdirs()
                    
                    val outputDirFile = if (customOutputDir == null && pageCount > 1) {
                        val baseDirName = inputFileName.substringBeforeLast(".")
                        var dir = File(publicDocsDir, baseDirName)
                        var index = 1
                        while (dir.exists()) {
                            dir = File(publicDocsDir, "${baseDirName}_$index")
                            index++
                        }
                        dir.mkdirs()
                        dir
                    } else null

                    engine.convertPdfToImages(
                        inputUri = inputUri,
                        format = format,
                        isZipMode = false,
                        outputStreamProvider = { _, suggestedName ->
                            val uri = if (customOutputDir != null) {
                                if (pageCount == 1) {
                                    // customOutputDir is a file URI
                                    finalOutputUri = customOutputDir
                                    finalOutputName = FileUriHelper.getFileName(context, customOutputDir)
                                    customOutputDir
                                } else {
                                    // customOutputDir is a tree URI
                                    val dir = DocumentFile.fromTreeUri(context, customOutputDir)
                                    val file = dir?.createFile("image/${format.extension}", suggestedName)
                                    file?.uri
                                }
                            } else {
                                if (pageCount == 1) {
                                    var f = File(publicDocsDir, suggestedName)
                                    var index = 1
                                    while (f.exists()) {
                                        f = File(publicDocsDir, "${suggestedName.substringBeforeLast(".")}_$index.${format.extension}")
                                        index++
                                    }
                                    finalOutputUri = Uri.fromFile(f)
                                    finalOutputName = f.name
                                    Uri.fromFile(f)
                                } else {
                                    val f = File(outputDirFile, suggestedName)
                                    Uri.fromFile(f)
                                }
                            }
                            context.contentResolver.openOutputStream(uri!!)!!
                        },
                        onProgress = { _conversionState.value = PdfGenerationState.Loading(it) }
                    ).onSuccess {
                        if (customOutputDir == null && pageCount > 1) {
                            finalOutputUri = Uri.fromFile(outputDirFile)
                            finalOutputName = outputDirFile!!.name
                            finalSize = outputDirFile.listFiles()?.sumOf { it.length() } ?: 0L
                        } else if (customOutputDir != null && pageCount > 1) {
                            finalOutputUri = customOutputDir
                            finalOutputName = FileUriHelper.getFileName(context, customOutputDir)
                            finalSize = 0L // Hard to calculate size of DocumentTree easily
                        } else if (pageCount == 1 && customOutputDir == null) {
                            finalSize = File(finalOutputUri!!.path!!).length()
                        } else if (pageCount == 1 && customOutputDir != null) {
                            // Can't easily get size from SAF file immediately without another query
                            finalSize = 0L 
                        }
                    }.onFailure { throw it }
                }

                if (finalOutputUri != null) {
                    val history = HistoryEntity(
                        fileName = finalOutputName,
                        filePath = finalOutputUri.toString(),
                        fileSize = finalSize,
                        timestamp = System.currentTimeMillis()
                    )
                    historyDao.insertHistory(history)
                    _conversionState.value = PdfGenerationState.Success(finalOutputUri!!)
                }
            } catch (e: Exception) {
                _conversionState.value = PdfGenerationState.Error(Exception(e.localizedMessage ?: "Conversion Failed", e))
            }
        }
    }
}
