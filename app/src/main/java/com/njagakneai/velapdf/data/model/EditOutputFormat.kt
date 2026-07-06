package com.njagakneai.velapdf.data.model

enum class EditOutputFormat(val extension: String, val displayName: String, val mimeType: String) {
    JPG("jpg", "JPG / JPEG", "image/jpeg"),
    PNG("png", "PNG (Lossless)", "image/png"),
    WEBP("webp", "WebP (Modern)", "image/webp"),
    PDF("pdf", "PDF Document", "application/pdf")
}
