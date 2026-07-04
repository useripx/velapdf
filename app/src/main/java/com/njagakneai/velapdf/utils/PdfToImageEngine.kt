package com.njagakneai.velapdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import com.njagakneai.velapdf.data.model.ImageFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class PdfToImageEngine(private val context: Context) {

    suspend fun convertPdfToImages(
        inputUri: Uri,
        format: ImageFormat,
        isZipMode: Boolean,
        outputZipFile: File? = null,
        outputStreamProvider: ((Int, String) -> OutputStream)? = null,
        onProgress: (Int) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        var fileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null
        var zipOutputStream: ZipOutputStream? = null

        try {
            fileDescriptor = context.contentResolver.openFileDescriptor(inputUri, "r")
                ?: throw Exception("Gagal membuka file PDF.")
            
            pdfRenderer = PdfRenderer(fileDescriptor)
            val pageCount = pdfRenderer.pageCount
            
            if (pageCount == 0) throw Exception("Dokumen PDF kosong.")

            if (isZipMode && outputZipFile == null) throw Exception("File ZIP output tidak ditentukan.")
            if (!isZipMode && outputStreamProvider == null) throw Exception("Penyedia output stream tidak ditentukan.")

            if (isZipMode) {
                zipOutputStream = ZipOutputStream(FileOutputStream(outputZipFile)).apply {
                    setLevel(Deflater.BEST_COMPRESSION)
                }
            }

            val compressFormat = when (format) {
                ImageFormat.JPG -> Bitmap.CompressFormat.JPEG
                ImageFormat.PNG -> Bitmap.CompressFormat.PNG
                ImageFormat.WEBP -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Bitmap.CompressFormat.WEBP_LOSSY
                    } else {
                        @Suppress("DEPRECATION")
                        Bitmap.CompressFormat.WEBP
                    }
                }
            }

            for (i in 0 until pageCount) {
                val page = pdfRenderer.openPage(i)
                // Density rendering setup
                val densityDpi = context.resources.displayMetrics.densityDpi
                val width = (page.width * densityDpi) / 72
                val height = (page.height * densityDpi) / 72
                
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                
                // Draw background white for formats that don't support transparency or just in case
                if (format == ImageFormat.JPG) {
                    bitmap.eraseColor(Color.WHITE)
                } else {
                    bitmap.eraseColor(Color.TRANSPARENT)
                }

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val fileName = "page_${i + 1}.${format.extension}"
                val outStream = if (isZipMode) {
                    val zipEntry = ZipEntry(fileName)
                    zipOutputStream!!.putNextEntry(zipEntry)
                    zipOutputStream
                } else {
                    outputStreamProvider!!(i, fileName)
                }
                
                bitmap.compress(compressFormat, 100, outStream)
                
                if (isZipMode) {
                    zipOutputStream!!.closeEntry()
                } else {
                    outStream.close()
                }
                
                bitmap.recycle()

                val progress = ((i + 1) * 100) / pageCount
                onProgress(progress)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            zipOutputStream?.close()
            pdfRenderer?.close()
            fileDescriptor?.close()
        }
    }
}
