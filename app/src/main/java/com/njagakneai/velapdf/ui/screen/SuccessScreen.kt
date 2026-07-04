package com.njagakneai.velapdf.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.R
import com.njagakneai.velapdf.utils.FileShareHelper
import com.njagakneai.velapdf.utils.FileUriHelper

@Composable
fun SuccessScreen(
    pdfUriString: String,
    onBackToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val uri = Uri.parse(pdfUriString)
    
    val fileName = FileUriHelper.getFileName(context, uri)
    val isZip = fileName.endsWith(".zip", ignoreCase = true)
    val isPdf = fileName.endsWith(".pdf", ignoreCase = true)
    val isImage = fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) || fileName.endsWith(".png", ignoreCase = true) || fileName.endsWith(".webp", ignoreCase = true)
    val isDirectory = !isZip && !isPdf && !isImage
    
    // Prevent accidental back causing restart
    BackHandler(onBack = { onBackToDashboard() })

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Success",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Konversi Berhasil!", 
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    val mimeType = when {
                        isZip -> "application/zip"
                        isPdf -> "application/pdf"
                        isImage -> "image/*"
                        else -> "vnd.android.document/directory"
                    }
                    val safeUri = FileUriHelper.getSafeUri(context, uri)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(safeUri, mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Tidak ada aplikasi yang cocok untuk membuka file/folder ini", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Gagal membuka: ${e.message}", Toast.LENGTH_LONG).show()
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when {
                        isZip -> "Buka ZIP"
                        isPdf -> "Buka PDF"
                        isImage -> "Buka Gambar"
                        else -> "Buka Folder"
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (!isDirectory) {
                Button(
                    onClick = {
                        FileShareHelper.shareFile(context, uri)
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        when {
                            isZip -> "Bagikan ZIP"
                            isPdf -> "Bagikan PDF"
                            isImage -> "Bagikan Gambar"
                            else -> "Bagikan"
                        }
                    )
                }
            } else {
                Button(
                    onClick = {
                        Toast.makeText(context, "Tidak dapat membagikan folder. Silakan buka folder terlebih dahulu.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bagikan Folder")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onBackToDashboard,
                colors = ButtonDefaults.outlinedButtonColors(),
            ) {
                Text("Kembali ke Beranda")
            }
        }
    }
}
