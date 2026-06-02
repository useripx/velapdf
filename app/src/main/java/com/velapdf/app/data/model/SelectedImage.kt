package com.velapdf.app.data.model

import android.net.Uri

data class SelectedImage(
    val originalUri: Uri,
    val cachedUri: Uri,
    val fileName: String,
    val orderIndex: Int,
    val rotationDegrees: Float = 0f
)
