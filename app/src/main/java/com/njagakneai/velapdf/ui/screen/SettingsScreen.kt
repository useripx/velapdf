package com.njagakneai.velapdf.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    var isAutoOpenEnabled by remember { mutableStateOf(true) }
    var expandedTheme by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf("Sistem") }
    
    var expandedQuality by remember { mutableStateOf(false) }
    var selectedQuality by remember { mutableStateOf("Medium") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Pengaturan",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preferensi Aplikasi
            item {
                Text(
                    text = "Preferensi Aplikasi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            // Theme Setting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Tema Aplikasi", style = MaterialTheme.typography.bodyLarge)
                            Text(text = selectedTheme, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Box {
                        TextButton(onClick = { expandedTheme = true }) {
                            Text("Ubah")
                        }
                        DropdownMenu(
                            expanded = expandedTheme,
                            onDismissRequest = { expandedTheme = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Terang") },
                                onClick = { selectedTheme = "Terang"; expandedTheme = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Gelap") },
                                onClick = { selectedTheme = "Gelap"; expandedTheme = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Sistem") },
                                onClick = { selectedTheme = "Sistem"; expandedTheme = false }
                            )
                        }
                    }
                }
            }

            item { HorizontalDivider() }

            // Preferensi Konversi
            item {
                Text(
                    text = "Preferensi Konversi",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            // Quality Setting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.HighQuality, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Kualitas Kompresi", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Gambar ke PDF", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Box {
                        TextButton(onClick = { expandedQuality = true }) {
                            Text(selectedQuality)
                        }
                        DropdownMenu(
                            expanded = expandedQuality,
                            onDismissRequest = { expandedQuality = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("High") },
                                onClick = { selectedQuality = "High"; expandedQuality = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Medium") },
                                onClick = { selectedQuality = "Medium"; expandedQuality = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Low") },
                                onClick = { selectedQuality = "Low"; expandedQuality = false }
                            )
                        }
                    }
                }
            }
            
            // Auto-open PDF
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Buka Otomatis", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Buka PDF setelah konversi selesai", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isAutoOpenEnabled,
                        onCheckedChange = { isAutoOpenEnabled = it }
                    )
                }
            }

            item { HorizontalDivider() }

            // Info Penyimpanan
            item {
                Text(
                    text = "Info Penyimpanan",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Lokasi Penyimpanan Default", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Documents/VelaPDF", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
