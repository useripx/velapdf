package com.njagakneai.velapdf.ui.screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.data.model.HistoryEntity
import java.text.SimpleDateFormat
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.njagakneai.velapdf.ui.viewmodel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val historyList by viewModel.historyList.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Konversi") }
            )
        }
    ) { paddingValues ->
        if (historyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada riwayat konversi.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyList) { item ->
                    HistoryItemCard(item)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: HistoryEntity) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val uri = Uri.parse(item.filePath)
                val mimeType = when {
                    item.fileName.endsWith(".pdf", ignoreCase = true) -> "application/pdf"
                    item.fileName.endsWith(".jpg", ignoreCase = true) || item.fileName.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
                    item.fileName.endsWith(".png", ignoreCase = true) -> "image/png"
                    item.fileName.endsWith(".webp", ignoreCase = true) -> "image/webp"
                    else -> "*/*"
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Tidak ada aplikasi untuk membuka file ini", Toast.LENGTH_SHORT).show()
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.fileName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            
            // Format Timestamp
            val date = Date(item.timestamp)
            val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val dateString = format.format(date)
            
            Text(text = "Tanggal: $dateString", style = MaterialTheme.typography.bodySmall)
            
            // Format File Size
            val sizeKb = item.fileSize / 1024
            Text(text = "Ukuran: $sizeKb KB", style = MaterialTheme.typography.bodySmall)
        }
    }
}
