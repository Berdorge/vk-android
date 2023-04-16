package com.happy.easter

import android.content.Context
import com.happy.easter.data.HappyEasterDatabaseProvider
import com.happy.easter.data.HappyEasterDownloadEntity
import com.happy.easter.data.HappyEasterList
import com.happy.easter.data.HappyEasterMetricData
import com.happy.easter.utils.getDeviceName
import com.happy.easter.utils.getVersionName
import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.math.max

internal class HappyEasterMetricQueue(
    private val context: Context
) {
    private var lastRequestTime: Long = 0
    private val executorService = Executors.newScheduledThreadPool(1)
    private val dao = HappyEasterDatabaseProvider.getDatabase(context)
        .metricDao()
    private val adapter = Moshi.Builder()
        .build()
        .adapter(HappyEasterList::class.java)
    private val coolDown
        get() = HappyEasterPerformance.queueConfiguration.queueRequestMinInterval

    init {
        executorService.submit(TrySendingMetrics())
    }

    fun push(metricData: HappyEasterMetricData) {
        executorService.submit {
            dao.insert(metricData)
            TrySendingMetrics().run()
        }
    }

    fun push(removedEntities: List<HappyEasterDownloadEntity>) {
        executorService.submit {
            val metricsEntities = removedEntities.map { entity ->
                HappyEasterMetricData(
                    duration = entity.endTime - entity.startTime,
                    url = entity.url,
                    requestBytes = 0,
                    responseBytes = entity.responseBytes,
                    responseCode = entity.code,
                    timestamp = ZonedDateTime.ofInstant(
                        Instant.ofEpochMilli(entity.startTime),
                        ZoneOffset.systemDefault()
                    ).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    version = context.getVersionName(),
                    device = getDeviceName(),
                    method = "DOWNLOAD",
                    feature = entity.feature,
                    contentType = "application/octet-stream"
                )
            }
            dao.batchInsert(metricsEntities)
            TrySendingMetrics().run()
        }
    }

    private fun hasCooledDown() = System.currentTimeMillis() - lastRequestTime >
        HappyEasterPerformance.queueConfiguration.queueRequestMinInterval

    private fun List<HappyEasterMetricData>.areEnough() = size >=
        HappyEasterPerformance.queueConfiguration.queueRequestMinSize

    private fun sendRequest(data: String): Boolean {
        lastRequestTime = System.currentTimeMillis()
        val url = BuildConfig.API_URL + "report"
        val contentType = "application/json; charset=utf-8"
        val obj = URL(url)
        return try {
            val con = obj.openConnection() as HttpURLConnection
            val dataBytes: ByteArray = data.toByteArray(StandardCharsets.UTF_8)

            con.requestMethod = "POST"
            con.setRequestProperty("Content-Type", contentType)
            con.setRequestProperty("X-User", "asdf")
            con.setRequestProperty("X-Array", "true")
            con.setRequestProperty("Content-Length", dataBytes.size.toString())
            con.doOutput = true
            con.outputStream.use {
                it.write(dataBytes)
            }

            val `in` = BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuilder()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            con.responseCode in 200 until 300
        } catch (e: Throwable) {
            e.printStackTrace()
            false
        }
    }

    private inner class TrySendingMetrics : Runnable {
        override fun run() {
            val metrics = dao.getAll()
            if (metrics.areEnough()) {
                if (hasCooledDown() && sendRequest(adapter.toJson(HappyEasterList(metrics)))) {
                    dao.delete(metrics)
                } else {
                    val coolDownTimeLeft = max(
                        0,
                        coolDown - (System.currentTimeMillis() - lastRequestTime)
                    )
                    executorService.schedule(TrySendingMetrics(), coolDownTimeLeft, MILLISECONDS)
                }
            }
        }
    }
}
