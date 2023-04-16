package com.happy.easter.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "metric", primaryKeys = ["url", "timestamp"])
internal data class HappyEasterMetricData(
    @Json(name = "duration")
    @ColumnInfo(name = "duration")
    val duration: Long,
    @Json(name = "url")
    @ColumnInfo(name = "url")
    val url: String,
    @Json(name = "size_sent")
    @ColumnInfo(name = "size_sent")
    val requestBytes: Long,
    @Json(name = "size_recieved")
    @ColumnInfo(name = "size_received")
    val responseBytes: Long,
    @Json(name = "http_code")
    @ColumnInfo(name = "http_code")
    val responseCode: Int?,
    @Json(name = "timestamp")
    @ColumnInfo(name = "timestamp")
    val timestamp: String,
    @Json(name = "version")
    @ColumnInfo(name = "version")
    val version: String,
    @Json(name = "device")
    @ColumnInfo(name = "device")
    val device: String,
    @Json(name = "method")
    @ColumnInfo(name = "method")
    val method: String,
    @Json(name = "features")
    @ColumnInfo(name = "features")
    val feature: String,
    @Json(name = "content_type")
    @ColumnInfo(name = "content_type")
    val contentType: String? = null
) : Parcelable
