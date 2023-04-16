package com.happy.easter

import com.happy.easter.data.HappyEasterMetricData
import com.happy.easter.utils.getDeviceName
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * A class representing metrics for a single HTTP request.
 */
class HappyEasterHttpMetric internal constructor(
    private val application: HappyEasterApplication,
    private val url: String,
    private var method: String
) {
    private var startTime: Long? = null
    private var endTime: Long? = null
    private var requestBytes: Long = 0
    private var responseBytes: Long = 0
    private var contentType: String? = null
    private var responseCode: Int? = null
    private var notSent = true
    private val feature = application.getFeature(url)

    /**
     * Starts the metric and records the current time as the start time.
     *
     * If the metric has already been started,
     * or the start time has been set manually,
     * this method does nothing.
     *
     * @see stop
     */
    fun start() {
        if (startTime == null) {
            startTime = System.currentTimeMillis()
        }
    }

    /**
     * Sets the start time of the metric.
     * It will override any value set by [start] or by a previous call to this method.
     *
     * @param time The start time of the metric, in milliseconds since the epoch.
     * @see setResponseEndTime
     */
    fun setRequestStartTime(time: Long) {
        startTime = time
    }

    /**
     * Sets the HTTP method of the request.
     * It will override any value set by the constructor or by a previous call to this method.
     *
     * @param method The HTTP method of the request.
     */
    fun setMethod(method: String?) {
        this.method = method.orEmpty()
    }

    /**
     * Sets the number of bytes sent in the request.
     * It will override any value set by a previous call to this method.
     *
     * @param bytes The number of bytes sent in the request.
     * @see setResponseBytes
     */
    fun setRequestBytes(bytes: Long) {
        requestBytes = bytes
    }

    /**
     * Sets the number of bytes received in the response.
     * It will override any value set by a previous call to this method.
     *
     * @param bytes The number of bytes received in the response.
     * @see setRequestBytes
     */
    fun setResponseBytes(bytes: Long) {
        responseBytes = bytes
    }

    /**
     * Sets the content type of the response.
     * It will override any value set by a previous call to this method.
     *
     * @param type The content type of the response.
     */
    fun setContentType(type: String?) {
        contentType = type
    }

    /**
     * Sets the HTTP response code of the response.
     * It will override any value set by a previous call to this method.
     *
     * @param code The HTTP response code of the response.
     */
    fun setResponseCode(code: Int) {
        responseCode = code
    }

    /**
     * Sets the end time of the metric.
     * It will override any value set by a previous call to this method.
     *
     * @param time The end time of the metric, in milliseconds since the epoch.
     * @see setRequestStartTime
     */
    fun setResponseEndTime(time: Long) {
        endTime = time
    }

    /**
     * Stops the metric and sends the recorded data to the queue.
     *
     * It is required to call [start] or [setRequestStartTime] before calling this method.
     *
     * If the end time has not been set manually by [setResponseEndTime],
     * this method will record the current time as the end time.
     *
     * If the metric has already been stopped, this method does nothing.
     *
     * @see start
     */
    fun stop() {
        if (notSent) {
            notSent = false
            val startTime = checkNotNull(startTime) {
                "start() or setRequestStartTime() must be called before stop()"
            }
            val endTime = endTime ?: System.currentTimeMillis()
            val callTime = endTime - startTime
            val data = HappyEasterMetricData(
                duration = callTime,
                url = url,
                requestBytes = requestBytes,
                responseBytes = responseBytes,
                responseCode = responseCode,
                timestamp = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(startTime),
                    ZoneOffset.systemDefault()
                ).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                version = application.getVersionName(),
                device = getDeviceName(),
                method = method,
                contentType = contentType,
                feature = feature
            )
            application.getBinder().sendHttpMetric(data)
        }
    }

    override fun toString(): String = "HappyEasterHttpMetric(" +
        "url='$url', " +
        "method=$method, " +
        "startTime=$startTime, " +
        "requestBytes=$requestBytes, " +
        "responseBytes=$responseBytes, " +
        "contentType=$contentType, " +
        "responseCode=$responseCode" +
        ")"
}
