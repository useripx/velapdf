package com.njagakneai.velapdf.data.repository

import android.net.Uri

sealed class PdfGenerationState {
    object Idle : PdfGenerationState()
    data class Loading(val progress: Int) : PdfGenerationState()
    data class Success(val uri: Uri) : PdfGenerationState()
    data class Error(val exception: Exception) : PdfGenerationState()
}
