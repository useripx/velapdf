package com.njagakneai.velapdf.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.njagakneai.velapdf.data.model.SelectedImage
import com.njagakneai.velapdf.data.repository.PdfGenerationState
import com.njagakneai.velapdf.data.repository.PdfRepository

@Composable
fun ConverterScreen(
    images: List<SelectedImage>,
    outputFileName: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { PdfRepository(context) }
    
    val state = repository.generatePdf(images, outputFileName)
        .collectAsState(initial = PdfGenerationState.Loading(0))
        
    BackHandler {
        // Prevent back intentionally
    }

    LaunchedEffect(state.value) {
        when (val currentState = state.value) {
            is PdfGenerationState.Success -> {
                onSuccess(currentState.uri.toString())
            }
            is PdfGenerationState.Error -> {
                onError(currentState.exception.message ?: "Unknown error")
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val progress = (state.value as? PdfGenerationState.Loading)?.progress ?: 0
            CircularProgressIndicator(progress = { progress.toFloat() / 100f })
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Converting to PDF... $progress%", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
