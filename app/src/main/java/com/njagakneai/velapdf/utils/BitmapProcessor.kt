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
}
