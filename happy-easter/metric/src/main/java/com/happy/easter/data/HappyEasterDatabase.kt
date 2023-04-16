package com.happy.easter.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HappyEasterDownloadEntity::class, HappyEasterMetricData::class],
    version = 1,
    exportSchema = false
)
internal abstract class HappyEasterDatabase : RoomDatabase() {
    abstract fun downloadDao(): HappyEasterDownloadDao
    abstract fun metricDao(): HappyEasterMetricDao
}
