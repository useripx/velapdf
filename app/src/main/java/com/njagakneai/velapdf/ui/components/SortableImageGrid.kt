package com.njagakneai.velapdf.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.njagakneai.velapdf.data.model.SelectedImage
import com.njagakneai.velapdf.utils.FileUriHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SortableImageGrid(
    selectedImages: List<SelectedImage>,
    onImagesUpdated: (List<SelectedImage>) -> Unit,
    modifier: Modifier = Modifier,
    maxSelection: Int = 50,
    onEdit: ((SelectedImage) -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val remainingQuota = maxSelection - selectedImages.size

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = if (remainingQuota > 0) remainingQuota else 1)
    ) { uris ->
        if (uris.isNotEmpty()) {
            isLoading = true
            coroutineScope.launch {
                val newSelectedImages = mutableListOf<SelectedImage>()
                val startIndex = selectedImages.size
                
                withContext(Dispatchers.IO) {
                    uris.take(remainingQuota).forEachIndexed { index, uri ->
                        val cachedUri = FileUriHelper.copyUriToCache(context, uri)
                        val fileName = FileUriHelper.getFileName(context, uri)
                        if (cachedUri != null) {
                            newSelectedImages.add(
                                SelectedImage(
                                    originalUri = uri,
                                    cachedUri = cachedUri,
                                    fileName = fileName,
                                    orderIndex = startIndex + index
                                )
                            )
                        }
                    }
                }
                
                onImagesUpdated(selectedImages + newSelectedImages)
                isLoading = false
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (selectedImages.isNotEmpty()) {
            Text(
                text = "${selectedImages.size} / $maxSelection gambar dipilih",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().heightIn(min = 200.dp)
        ) {
            itemsIndexed(selectedImages) { index, image ->
                SortableImageItem(
                    image = image,
                    index = index,
                    totalItems = selectedImages.size,
                    onRemove = {
                        val updatedList = selectedImages.toMutableList().apply { removeAt(index) }
                        onImagesUpdated(updatedList)
                    },
                    onRotate = {
                        val currentRotation = image.rotationDegrees
                        val newRotation = (currentRotation + 90f) % 360f
                        val updatedList = selectedImages.toMutableList().apply {
                            set(index, image.copy(rotationDegrees = newRotation))
                        }
                        onImagesUpdated(updatedList)
                    },
                    onMoveUp = {
                        if (index > 0) {
                            val updatedList = selectedImages.toMutableList()
                            val temp = updatedList[index]
                            updatedList[index] = updatedList[index - 1]
                            updatedList[index - 1] = temp
                            onImagesUpdated(updatedList)
                        }
                    },
                    onMoveDown = {
                        if (index < selectedImages.size - 1) {
                            val updatedList = selectedImages.toMutableList()
                            val temp = updatedList[index]
                            updatedList[index] = updatedList[index + 1]
                            updatedList[index + 1] = temp
                            onImagesUpdated(updatedList)
                        }
                    },
                    onEdit = {
                        onEdit?.invoke(image)
                    }
                )
            }

            if (selectedImages.size < maxSelection) {
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .clickable {
                                multiplePhotoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambahkan Gambar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tambah",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortableImageItem(
    image: SelectedImage,
    index: Int,
    totalItems: Int,
    onRemove: () -> Unit,
    onRotate: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEdit: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        AsyncImage(
            model = image.cachedUri,
            contentDescription = image.fileName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = image.rotationDegrees
                }
        )

        // Overlay Badge (Index)
        Box(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopStart)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${index + 1}",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }

        // Close/Remove Button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .padding(2.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus",
                tint = MaterialTheme.colorScheme.onError,
                modifier = Modifier.size(16.dp)
            )
        }

        // Action Row (Bottom)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMoveUp,
                enabled = index > 0,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Pindah Atas",
                    tint = if (index > 0) Color.White else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onMoveDown,
                enabled = index < totalItems - 1,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Pindah Bawah",
                    tint = if (index < totalItems - 1) Color.White else Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (onEdit != null) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            IconButton(
                onClick = onRotate,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rotasi",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
