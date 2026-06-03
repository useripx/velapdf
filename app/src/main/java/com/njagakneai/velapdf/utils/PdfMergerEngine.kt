package com.njagakneai.velapdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.njagakneai.velapdf.data.model.DocumentType
import com.njagakneai.velapdf.data.model.MergeableDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Engine for merging multiple PDF and Image files into a single PDF document.
 * Uses native Android APIs: PdfRenderer (read) and PdfDocument (write).
 *
 * Capacity limits:
 * - Max 100 files
 * - Max 500 pages per file
 * - Max 500 total pages in output
 */
object PdfMergerEngine {

    const val MAX_FILES = 100
    const val MAX_PAGES_PER_FILE = 500
    const val MAX_TOTAL_PAGES = 500

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val totalPages: Int = 0
    )

    /**
     * Validates the document list against capacity limits.
     */
    fun validate(documents: List<MergeableDocument>): ValidationResult {
        if (documents.isEmpty()) {
            return ValidationResult(false, "Tidak ada dokumen untuk digabungkan")
        }
        if (documents.size > MAX_FILES) {
            return ValidationResult(false, "Maksimal $MAX_FILES file yang dapat digabungkan")
        }

        var totalPages = 0
        for (doc in documents) {
            if (doc.pageCount > MAX_PAGES_PER_FILE) {
                return ValidationResult(
                    false,
                    "File \"${doc.fileName}\" memiliki ${doc.pageCount} halaman (maks $MAX_PAGES_PER_FILE)"
                )
            }
            totalPages += doc.pageCount
        }

        if (totalPages > MAX_TOTAL_PAGES) {
            return ValidationResult(
                false,
                "Total halaman ($totalPages) melebihi batas maksimal $MAX_TOTAL_PAGES halaman"
            )
        }

        return ValidationResult(true, totalPages = totalPages)
    }

    /**
     * Gets the page count of a PDF file from its URI.
     */
    fun getPdfPageCount(context: Context, document: MergeableDocument): Int {
        if (document.type != DocumentType.PDF) return 1

        return try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(document.cachedUri, "r")
            fileDescriptor?.use { fd ->
                val renderer = PdfRenderer(fd)
                val count = renderer.pageCount
                renderer.close()
                count
            } ?: 1
        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    /**
     * Merges all documents into a single PDF file.
     * Images are rendered as full pages. PDFs are rendered page-by-page as bitmaps.
     *
     * @param context Application context
     * @param documents Ordered list of documents to merge
     * @param outputFile Target output file
     * @param onProgress Progress callback (0-100)
     * @return The output file
     */
    suspend fun mergeDocuments(
        context: Context,
        documents: List<MergeableDocument>,
        outputFile: File,
        onProgress: suspend (Int) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val validation = validate(documents)
        if (!validation.isValid) {
            throw IllegalArgumentException(validation.errorMessage)
        }

        val totalPages = validation.totalPages
        var processedPages = 0
        val pdfDocument = PdfDocument()

        try {
            for (doc in documents) {
                when (doc.type) {
                    DocumentType.IMAGE -> {
                        val bitmap = loadImageBitmap(context, doc)
                        addBitmapPage(pdfDocument, bitmap, processedPages + 1)
                        bitmap.recycle()
                        processedPages++
                        onProgress(((processedPages * 100) / totalPages).coerceAtMost(100))
                    }

                    DocumentType.PDF -> {
                        val fd = context.contentResolver.openFileDescriptor(doc.cachedUri, "r")
                        fd?.use { fileDescriptor ->
                            val renderer = PdfRenderer(fileDescriptor)
                            for (pageIndex in 0 until renderer.pageCount) {
                                val page = renderer.openPage(pageIndex)
                                // Render PDF page to bitmap at 2x scale for quality
                                val scale = 2
                                val bitmapWidth = page.width * scale
                                val bitmapHeight = page.height * scale
                                val bitmap = Bitmap.createBitmap(
                                    bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888
                                )
                                // Fill with white background
                                val canvas = Canvas(bitmap)
                                canvas.drawColor(Color.WHITE)
                                page.render(
                                    bitmap, null, null,
                                    PdfRenderer.Page.RENDER_MODE_FOR_PRINT
                                )
                                page.close()

                                addBitmapPage(pdfDocument, bitmap, processedPages + 1)
                                bitmap.recycle()
                                processedPages++
                                onProgress(
                                    ((processedPages * 100) / totalPages).coerceAtMost(100)
                                )
                            }
                            renderer.close()
                        }
                    }
                }
            }

            val outputStream = FileOutputStream(outputFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()

            outputFile
        } finally {
            pdfDocument.close()
        }
    }

    /**
     * Adds a bitmap as a page to the PdfDocument.
     */
    private fun addBitmapPage(pdfDocument: PdfDocument, bitmap: Bitmap, pageNumber: Int) {
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
    }

    /**
     * Loads an image URI as a Bitmap.
     */
    private fun loadImageBitmap(context: Context, doc: MergeableDocument): Bitmap {
        val inputStream = context.contentResolver.openInputStream(doc.cachedUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmap ?: throw IllegalStateException("Tidak dapat memuat gambar: ${doc.fileName}")
    }
}
