package com.njagakneai.velapdf.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.njagakneai.velapdf.data.model.HistoryEntity

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
