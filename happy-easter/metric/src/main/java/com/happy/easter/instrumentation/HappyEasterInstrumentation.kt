package com.happy.easter.instrumentation

import android.app.DownloadManager
import android.net.Uri
import com.happy.easter.HappyEasterApplication
import com.happy.easter.utils.beginMetric
import com.happy.easter.utils.onResponse
import okhttp3.Call
import okhttp3.Callback
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import javax.net.ssl.HttpsURLConnection

internal object HappyEasterInstrumentation {
    @JvmStatic
    fun execute(call: Call): okhttp3.Response {
        val metric = call.beginMetric()
        return call.execute()
            .let(metric::onResponse)
    }

    @JvmStatic
    fun enqueue(call: Call, callback: Callback) {
        val metric = call.beginMetric()
        call.enqueue(object : Callback by callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                callback.onResponse(call, metric.onResponse(response))
            }
        })
    }

    @JvmStatic
    fun openConnection(url: URL): URLConnection {
        val delegate = url.openConnection()
        return if (HappyEasterApplication.isInitialized()) {
            when (delegate) {
                is HttpsURLConnection -> InstrumentedHttpsUrlConnection(url, delegate)
                is HttpURLConnection -> InstrumentedHttpUrlConnection(url, delegate)
                else -> delegate
            }
        } else {
            delegate
        }
    }

    @JvmStatic
    fun downloadManagerRequest(
        uri: Uri
    ): DownloadManager.Request = InstrumentedDownloadManagerRequest(uri)

    @JvmStatic
    fun enqueue(
        downloadManager: DownloadManager,
        request: DownloadManager.Request
    ): Long {
        return downloadManager.enqueue(request)
            .also { id ->
                if (request is InstrumentedDownloadManagerRequest) {
                    val url = request.uri.toString()
                    val feature = HappyEasterApplication.getInstance()
                        .getFeature(url)
                    HappyEasterApplication.getInstance()
                        .getBinder()
                        .onDownloadEnqueued(id, url, feature)
                }
            }
    }
}
