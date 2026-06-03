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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import com.njagakneai.velapdf.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isAutoOpenEnabled by viewModel.isAutoOpenEnabled.collectAsState()
    val selectedTheme by viewModel.appTheme.collectAsState()
    val selectedQuality by viewModel.compressionQuality.collectAsState()
    val selectedPageSize by viewModel.pageSize.collectAsState()
    val customWidth by viewModel.customPageWidth.collectAsState()
    val customHeight by viewModel.customPageHeight.collectAsState()

    var expandedTheme by remember { mutableStateOf(false) }
    var expandedQuality by remember { mutableStateOf(false) }
    var expandedPageSize by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }

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
                                onClick = { viewModel.setAppTheme("Terang"); expandedTheme = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Gelap") },
                                onClick = { viewModel.setAppTheme("Gelap"); expandedTheme = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Sistem") },
                                onClick = { viewModel.setAppTheme("Sistem"); expandedTheme = false }
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
                                text = { Text("Tinggi") },
                                onClick = { viewModel.setCompressionQuality("Tinggi"); expandedQuality = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Sedang") },
                                onClick = { viewModel.setCompressionQuality("Sedang"); expandedQuality = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Rendah") },
                                onClick = { viewModel.setCompressionQuality("Rendah"); expandedQuality = false }
                            )
                        }
                    }
                }
            }

            // Page Size Setting
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.InsertPageBreak, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = "Ukuran Halaman", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Standar halaman PDF", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Box {
                        TextButton(onClick = { expandedPageSize = true }) {
                            Text(selectedPageSize)
                        }
                        DropdownMenu(
                            expanded = expandedPageSize,
                            onDismissRequest = { expandedPageSize = false }
                        ) {
                            listOf("A4", "A5", "B5", "F4", "Legal", "Letter", "Custom").forEach { size ->
                                DropdownMenuItem(
                                    text = { Text(size) },
                                    onClick = { viewModel.setPageSize(size); expandedPageSize = false }
                                )
                            }
                        }
                    }
                }
            }

            // Custom Page Size Inputs
            if (selectedPageSize == "Custom") {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = customWidth.toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { viewModel.setCustomPageWidth(it) }
                            },
                            label = { Text("Lebar (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customHeight.toString(),
                            onValueChange = { newValue ->
                                newValue.toIntOrNull()?.let { viewModel.setCustomPageHeight(it) }
                            },
                            label = { Text("Tinggi (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
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
                        onCheckedChange = { viewModel.setAutoOpenEnabled(it) }
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
            item { HorizontalDivider() }

            // Manajemen Data
            item {
                Text(
                    text = "Manajemen Data",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
            }
            
            item {
                Button(
                    onClick = { showWipeConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Bersihkan Cache & Riwayat")
                }
                Text(
                    text = "Tindakan ini akan menghapus semua riwayat konversi lokal dan mereset pengaturan ke bawaan. File PDF asli tidak akan terhapus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus semua cache, riwayat, dan mereset pengaturan? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showWipeConfirmDialog = false
                        viewModel.wipeCache {
                            // Optionally navigate away or show toast
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Bersihkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
