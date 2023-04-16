package ru.ok.android.itmohack2023

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.happy.easter.HappyEasterPerformance


class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val webView = findViewById<WebView>(R.id.web_view)
        webView.webViewClient = MetricWebViewClient()
        webView.settings.javaScriptEnabled = true
        val htmlText = resources.assets.open("webview.html")
            .bufferedReader()
            .use { it.readText() }
        webView.loadDataWithBaseURL("", htmlText, "text/html", "UTF-8", "")
    }

    private class MetricWebViewClient : WebViewClient() {
        private val performance = HappyEasterPerformance.getInstance()

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            val time = System.currentTimeMillis()
            val metric = performance.newHttpMetric(request?.url.toString(), "WEBVIEW")
            metric.setRequestStartTime(time)
            metric.setResponseEndTime(time)
            metric.stop()
            return super.shouldInterceptRequest(view, request)
        }
    }
}