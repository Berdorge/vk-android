package com.happy.easter.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
internal data class HappyEasterDownloadEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "start_time")
    val startTime: Long,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "feature")
    val feature: String,
    @ColumnInfo(name = "end_time")
    val endTime: Long = startTime,
    @ColumnInfo(name = "response_bytes")
    val responseBytes: Long = 0,
    @ColumnInfo(name = "code")
    val code: Int = -1,
)
