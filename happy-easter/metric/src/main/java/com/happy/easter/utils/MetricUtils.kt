package com.happy.easter.utils

import com.happy.easter.HappyEasterHttpMetric
import com.happy.easter.HappyEasterPerformance
import okhttp3.Call
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

internal fun Call.beginMetric() = HappyEasterPerformance.getInstance()
    .newHttpMetric(request().url.toString(), request().method)
    .apply {
        start()
        setRequestBytes(request().body?.contentLength() ?: 0)
    }

internal fun HappyEasterHttpMetric.onResponse(response: Response): Response {
    val body = response.body?.bytes()
    setContentType(response.header("Content-Type"))
    setResponseCode(response.code)
    setResponseBytes(body?.size?.toLong() ?: 0)
    stop()
    return response.newBuilder()
        .body(body?.toResponseBody(response.body?.contentType()))
        .build()
}
