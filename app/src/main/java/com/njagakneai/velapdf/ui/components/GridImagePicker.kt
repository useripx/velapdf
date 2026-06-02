package com.njagakneai.velapdf.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
// Removed icons import
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun GridImagePicker(
    selectedImages: List<SelectedImage>,
    onImagesSelected: (List<SelectedImage>) -> Unit,
    modifier: Modifier = Modifier,
    maxSelection: Int = 50
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelection)
    ) { uris ->
        if (uris.isNotEmpty()) {
            isLoading = true
            coroutineScope.launch {
                val newSelectedImages = mutableListOf<SelectedImage>()
                val startIndex = selectedImages.size
                
                withContext(Dispatchers.IO) {
                    uris.forEachIndexed { index, uri ->
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
                
                onImagesSelected(selectedImages + newSelectedImages)
                isLoading = false
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(selectedImages) { image ->
                ImageThumbnail(image = image)
            }

            item {
                AddImageButton {
                    multiplePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageThumbnail(image: SelectedImage) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = image.cachedUri,
            contentDescription = image.fileName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "+",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tambah",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
