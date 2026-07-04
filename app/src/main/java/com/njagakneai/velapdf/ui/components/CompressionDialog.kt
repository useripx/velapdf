package com.njagakneai.velapdf.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.data.model.CompressionLevel

@Composable
fun CompressionDialog(
    onDismiss: () -> Unit,
    onConfirm: (CompressionLevel) -> Unit
) {
    var selectedLevel by remember { mutableStateOf(CompressionLevel.BIASA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Pilih Tingkat Kompresi", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pilih tingkat kompresi untuk file PDF Anda:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Option: Biasa
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLevel = CompressionLevel.BIASA }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLevel == CompressionLevel.BIASA,
                        onClick = { selectedLevel = CompressionLevel.BIASA }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = CompressionLevel.BIASA.title, fontWeight = FontWeight.Medium)
                        Text(
                            text = "Kualitas tinggi, ukuran file lebih besar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Option: Super
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLevel = CompressionLevel.SUPER }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLevel == CompressionLevel.SUPER,
                        onClick = { selectedLevel = CompressionLevel.SUPER }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = CompressionLevel.SUPER.title, fontWeight = FontWeight.Medium)
                        Text(
                            text = "Ukuran file sangat kecil, cocok untuk dokumen.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedLevel) }
            ) {
                Text("Lanjutkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
