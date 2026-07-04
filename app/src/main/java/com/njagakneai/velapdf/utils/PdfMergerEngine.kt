package com.njagakneai.velapdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
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
        compressionLevel: com.njagakneai.velapdf.data.model.CompressionLevel,
        onProgress: suspend (Int) -> Unit
    ): File = withContext(Dispatchers.IO) {
        val validation = validate(documents)
        if (!validation.isValid) {
            throw IllegalArgumentException(validation.errorMessage)
        }

        val totalPages = validation.totalPages
        var processedPages = 0
        
        val pdfWriter = PdfWriter(outputFile)
        val mainPdfDoc = PdfDocument(pdfWriter)
        val merger = PdfMerger(mainPdfDoc)

        val quality = if (compressionLevel == com.njagakneai.velapdf.data.model.CompressionLevel.SUPER) 60 else 75

        try {
            for (doc in documents) {
                when (doc.type) {
                    DocumentType.IMAGE -> {
                        var bitmap = loadImageBitmap(context, doc)
                        
                        if (compressionLevel == com.njagakneai.velapdf.data.model.CompressionLevel.SUPER && bitmap.width > 1500) {
                            val ratio = 1500f / bitmap.width
                            val scaledHeight = (bitmap.height * ratio).toInt()
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1500, scaledHeight, true)
                            if (scaledBitmap != bitmap) {
                                bitmap.recycle()
                                bitmap = scaledBitmap
                            }
                        }

                        val baos = java.io.ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
                        bitmap.recycle() // free memory
                        
                        val compressedArray = baos.toByteArray()
                        
                        // Create a temporary PDF in memory for the image
                        val baosPdf = java.io.ByteArrayOutputStream()
                        val tempPdfDoc = PdfDocument(PdfWriter(baosPdf))
                        val tempDoc = Document(tempPdfDoc)
                        tempDoc.setMargins(0f, 0f, 0f, 0f)
                        
                        val imageData = ImageDataFactory.create(compressedArray)
                        val pdfImage = Image(imageData)
                        val pageSize = PageSize(pdfImage.imageWidth, pdfImage.imageHeight)
                        tempPdfDoc.addNewPage(pageSize)
                        pdfImage.setFixedPosition(1, 0f, 0f)
                        tempDoc.add(pdfImage)
                        tempDoc.close()
                        
                        // Merge the temp PDF
                        val srcPdfDoc = PdfDocument(PdfReader(java.io.ByteArrayInputStream(baosPdf.toByteArray())))
                        merger.merge(srcPdfDoc, 1, srcPdfDoc.numberOfPages)
                        srcPdfDoc.close()
                        
                        processedPages++
                        onProgress(((processedPages * 100) / totalPages).coerceAtMost(100))
                    }

                    DocumentType.PDF -> {
                        val inputStream = context.contentResolver.openInputStream(doc.cachedUri)
                        if (inputStream != null) {
                            val srcPdfDoc = PdfDocument(PdfReader(inputStream))
                            val pagesToMerge = srcPdfDoc.numberOfPages
                            merger.merge(srcPdfDoc, 1, pagesToMerge)
                            srcPdfDoc.close()
                            inputStream.close()
                            
                            processedPages += pagesToMerge
                            onProgress(((processedPages * 100) / totalPages).coerceAtMost(100))
                        }
                    }
                }
            }

            outputFile
        } finally {
            merger.close()
            mainPdfDoc.close()
        }
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
