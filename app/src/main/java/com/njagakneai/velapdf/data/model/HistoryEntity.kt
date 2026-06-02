package com.njagakneai.velapdf.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fileName: String,
    val filePath: String,
    val timestamp: Long,
    val fileSize: Long
)
