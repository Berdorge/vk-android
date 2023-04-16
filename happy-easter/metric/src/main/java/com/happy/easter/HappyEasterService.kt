package com.happy.easter

import android.app.Service
import android.content.Intent
import com.happy.easter.data.HappyEasterMetricData
import com.happy.easter.data.DownloadManagerTracker

internal class HappyEasterService : Service() {
    private val binder = HappyEasterBinder()
    private lateinit var queue: HappyEasterMetricQueue
    private lateinit var downloadManagerTracker: DownloadManagerTracker

    override fun onBind(p0: Intent?) = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        queue = HappyEasterMetricQueue(this)
        downloadManagerTracker = DownloadManagerTracker(this, queue)
        return START_NOT_STICKY
    }

    inner class HappyEasterBinder : HappyEasterServiceInterface.Stub() {
        override fun sendHttpMetric(metricData: HappyEasterMetricData?) {
            if (metricData != null) {
                queue.push(metricData)
            }
        }

        override fun onDownloadEnqueued(
            downloadId: Long,
            url: String,
            feature: String
        ) {
            downloadManagerTracker.onDownloadEnqueued(downloadId, url, feature)
        }
    }
}
