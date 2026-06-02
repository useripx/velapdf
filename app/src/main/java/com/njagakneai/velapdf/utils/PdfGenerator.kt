package com.njagakneai.velapdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.pdf.PdfDocument
import com.njagakneai.velapdf.data.model.SelectedImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    suspend fun generatePdfFromImages(
        context: Context,
        images: List<SelectedImage>,
        outputFile: File,
        onProgress: suspend (Int) -> Unit
    ): File = withContext(Dispatchers.IO) {
        if (images.isEmpty()) throw IllegalArgumentException("No images to convert")

        val pdfDocument = PdfDocument()

        try {
            images.forEachIndexed { index, selectedImage ->
                val bitmap = getBitmapFromUri(context, selectedImage)
                
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                
                val canvas = page.canvas
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
                
                bitmap.recycle()
                
                val progress = ((index + 1) * 100) / images.size
                onProgress(progress)
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
    
    private fun getBitmapFromUri(context: Context, selectedImage: SelectedImage): Bitmap {
        val inputStream = context.contentResolver.openInputStream(selectedImage.cachedUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        if (bitmap == null) {
            throw IllegalStateException("Could not decode bitmap")
        }
        
        if (selectedImage.rotationDegrees != 0f) {
            val matrix = Matrix()
            matrix.postRotate(selectedImage.rotationDegrees)
            val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            // Recycle original if it's not the same instance
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }
            return rotatedBitmap
        }
        
        return bitmap
    }
}
