package com.njagakneai.velapdf.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object FileUriHelper {

    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = cursor.getString(index)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "image_${System.currentTimeMillis()}.jpg"
    }

    fun copyUriToCache(context: Context, uri: Uri): Uri? {
        return try {
            val fileName = getFileName(context, uri)
            // Rename to ensure unique temporary files
            val uniqueFileName = "${UUID.randomUUID()}_$fileName"
            val cacheFile = File(context.cacheDir, uniqueFileName)
            
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(cacheFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTempCameraUri(context: Context): Uri {
        val imagesDir = File(context.cacheDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        val tempFile = File(imagesDir, "camera_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    fun getSafeUri(context: Context, uri: Uri): Uri {
        return if (uri.scheme == "file") {
            try {
                FileProvider.getUriForFile(context, "${context.packageName}.provider", File(uri.path!!))
            } catch (e: Exception) {
                uri
            }
        } else {
            uri
        }
    }
}
