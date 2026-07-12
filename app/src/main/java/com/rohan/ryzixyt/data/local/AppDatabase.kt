package com.rohan.ryzixyt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WatchHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchHistoryDao(): WatchHistoryDao
}
