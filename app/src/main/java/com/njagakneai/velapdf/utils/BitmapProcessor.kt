package com.njagakneai.velapdf.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import com.njagakneai.velapdf.data.model.EditOutputFormat
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize

object BitmapProcessor {

    suspend fun rotateBitmap(context: Context, uri: Uri, degrees: Float): Uri? = withContext(Dispatchers.IO) {
        if (degrees == 0f) return@withContext uri

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val matrix = Matrix()
                matrix.postRotate(degrees)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                
                // Save rotated bitmap to a new cache file
                val fileName = "rotated_${UUID.randomUUID()}.jpg"
                val cacheFile = File(context.cacheDir, fileName)
                
                FileOutputStream(cacheFile).use { out ->
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                }
                
                bitmap.recycle() // free resources
                rotatedBitmap.recycle()
                
                return@withContext Uri.fromFile(cacheFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    suspend fun compressBitmap(bitmap: Bitmap, quality: Int = 80): ByteArray = withContext(Dispatchers.IO) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val byteArray = stream.toByteArray()
        bitmap.recycle()
        return@withContext byteArray
    }

    suspend fun saveBitmapAs(context: Context, bitmap: Bitmap, format: EditOutputFormat, fileName: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, "$fileName.${format.extension}")
            FileOutputStream(cacheFile).use { out ->
                val compressFormat = when (format) {
                    EditOutputFormat.PNG -> Bitmap.CompressFormat.PNG
                    EditOutputFormat.WEBP -> if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                        Bitmap.CompressFormat.WEBP_LOSSLESS
                    } else {
                        Bitmap.CompressFormat.WEBP
                    }
                    else -> Bitmap.CompressFormat.JPEG
                }
                val quality = if (format == EditOutputFormat.PNG) 100 else 90
                bitmap.compress(compressFormat, quality, out)
            }
            return@withContext Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun saveBitmapAsPdf(context: Context, bitmap: Bitmap, fileName: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val cacheFile = File(context.cacheDir, "$fileName.pdf")
            val pdfWriter = PdfWriter(cacheFile)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            document.setMargins(0f, 0f, 0f, 0f)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val imageData = ImageDataFactory.create(stream.toByteArray())
            val pdfImage = Image(imageData)
            
            val pageSize = PageSize(pdfImage.imageWidth, pdfImage.imageHeight)
            pdfDocument.addNewPage(pageSize)
            
            pdfImage.setFixedPosition(1, 0f, 0f)
            document.add(pdfImage)
            document.close()
            
            return@withContext Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
