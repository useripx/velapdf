package com.njagakneai.velapdf.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object FileShareHelper {
    fun shareFile(context: Context, uri: Uri) {
        val fileName = FileUriHelper.getFileName(context, uri)
        val mimeType = when {
            fileName.endsWith(".zip", ignoreCase = true) -> "application/zip"
            fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
            fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) || fileName.endsWith(".png", ignoreCase = true) || fileName.endsWith(".webp", ignoreCase = true) -> "image/*"
            else -> "*/*"
        }
        
        val safeUri = FileUriHelper.getSafeUri(context, uri)
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, safeUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        val chooser = Intent.createChooser(shareIntent, "Bagikan via")
        try {
            context.startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Tidak ada aplikasi untuk membagikan file", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
