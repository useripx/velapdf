package com.njagakneai.velapdf.data.model

import android.net.Uri

/**
 * Represents a document (PDF or Image) that can be merged.
 * Images are treated as single-page documents that will be auto-converted to PDF during merge.
 */
data class MergeableDocument(
    val originalUri: Uri,
    val cachedUri: Uri,
    val fileName: String,
    val pageCount: Int = 1,
    val fileSizeBytes: Long = 0L,
    val type: DocumentType = DocumentType.IMAGE,
    val orderIndex: Int = 0
)

enum class DocumentType {
    PDF,
    IMAGE
}
