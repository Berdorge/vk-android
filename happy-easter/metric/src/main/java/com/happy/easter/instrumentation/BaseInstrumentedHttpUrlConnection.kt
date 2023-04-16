package com.happy.easter.instrumentation

import android.annotation.TargetApi
import android.os.Build
import com.happy.easter.HappyEasterPerformance
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.Permission

internal class BaseInstrumentedHttpUrlConnection(
    url: URL,
    private val delegate: HttpURLConnection
) : InstrumentedUrlConnection {
    private val metric = HappyEasterPerformance.getInstance()
        .newHttpMetric(url.toString(), delegate.requestMethod)

    override fun getContentEncoding(): String? {
        return delegate.contentEncoding
    }

    override fun getHeaderField(name: String?): String? {
        return delegate.getHeaderField(name)
    }

    override fun getReadTimeout(): Int {
        return delegate.readTimeout
    }

    override fun connect() {
        metric.start()
        delegate.connect()
    }

    override fun getUseCaches(): Boolean {
        return delegate.useCaches
    }

    override fun setConnectTimeout(timeout: Int) {
        delegate.connectTimeout = timeout
    }

    override fun getDate(): Long {
        return delegate.date
    }

    override fun getExpiration(): Long {
        return delegate.expiration
    }

    override fun getContent(): Any {
        return delegate.content
    }

    override fun getContent(classes: Array<out Class<Any>>?): Any {
        return delegate.getContent(classes)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun getContentLengthLong(): Long {
        return delegate.contentLengthLong
    }

    override fun getHeaderFieldInt(name: String?, Default: Int): Int {
        return delegate.getHeaderFieldInt(name, Default)
    }

    override fun setUseCaches(usecaches: Boolean) {
        delegate.useCaches = usecaches
    }

    override fun getIfModifiedSince(): Long {
        return delegate.ifModifiedSince
    }

    override fun setIfModifiedSince(ifmodifiedsince: Long) {
        return delegate.setIfModifiedSince(ifmodifiedsince)
    }

    override fun getDoInput(): Boolean {
        return delegate.doInput
    }

    override fun getLastModified(): Long {
        return delegate.lastModified
    }

    override fun setDefaultUseCaches(defaultusecaches: Boolean) {
        delegate.defaultUseCaches = defaultusecaches
    }

    override fun setDoOutput(dooutput: Boolean) {
        delegate.doOutput = dooutput
    }

    override fun getDefaultUseCaches(): Boolean {
        return delegate.defaultUseCaches
    }

    override fun getRequestProperties(): MutableMap<String, MutableList<String>> {
        return delegate.requestProperties
    }

    override fun setReadTimeout(timeout: Int) {
        delegate.readTimeout = timeout
    }

    override fun getDoOutput(): Boolean {
        return delegate.doOutput
    }

    override fun addRequestProperty(key: String?, value: String?) {
        delegate.addRequestProperty(key, value)
    }

    override fun getConnectTimeout(): Int {
        return delegate.connectTimeout
    }

    override fun setDoInput(doinput: Boolean) {
        delegate.doInput = doinput
    }

    override fun getHeaderFields(): MutableMap<String, MutableList<String>> {
        return delegate.headerFields
    }

    override fun getInputStream(): InputStream {
        metric.start()
        return InstrumentedInputStream(delegate.inputStream) {
            metric.setResponseBytes(it)
            metric.setContentType(delegate.contentType)
            metric.setResponseCode(delegate.responseCode)
            metric.stop()
        }
    }

    override fun getAllowUserInteraction(): Boolean {
        return delegate.allowUserInteraction
    }

    override fun getURL(): URL {
        return delegate.url
    }

    override fun setRequestProperty(key: String?, value: String?) {
        delegate.setRequestProperty(key, value)
    }

    override fun setAllowUserInteraction(allowuserinteraction: Boolean) {
        delegate.allowUserInteraction
    }

    override fun getContentLength(): Int {
        return delegate.contentLength
    }

    override fun getContentType(): String {
        return delegate.contentType
    }

    override fun getRequestProperty(key: String?): String {
        return delegate.getRequestProperty(key)
    }

    override fun getOutputStream(): OutputStream {
        metric.start()
        return InstrumentedOutputStream(metric, delegate.outputStream)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun getHeaderFieldLong(name: String?, Default: Long): Long {
        return delegate.getHeaderFieldLong(name, Default)
    }

    override fun getHeaderField(n: Int): String {
        return delegate.getHeaderField(n)
    }

    override fun usingProxy(): Boolean {
        return delegate.usingProxy()
    }

    override fun getHeaderFieldKey(n: Int): String {
        return delegate.getHeaderFieldKey(n)
    }

    override fun setInstanceFollowRedirects(followRedirects: Boolean) {
        delegate.instanceFollowRedirects = followRedirects
    }

    override fun getHeaderFieldDate(name: String?, Default: Long): Long {
        return delegate.getHeaderFieldDate(name, Default)
    }

    override fun setChunkedStreamingMode(chunklen: Int) {
        delegate.setChunkedStreamingMode(chunklen)
    }

    override fun getPermission(): Permission {
        return delegate.permission
    }

    override fun getInstanceFollowRedirects(): Boolean {
        return delegate.instanceFollowRedirects
    }

    override fun getRequestMethod(): String {
        return delegate.requestMethod
    }

    override fun getErrorStream(): InputStream {
        return delegate.errorStream
    }

    override fun getResponseMessage(): String {
        return delegate.responseMessage
    }

    override fun setFixedLengthStreamingMode(contentLength: Int) {
        return delegate.setFixedLengthStreamingMode(contentLength)
    }

    override fun setFixedLengthStreamingMode(contentLength: Long) {
        return delegate.setFixedLengthStreamingMode(contentLength)
    }

    override fun disconnect() {
        metric.stop()
        delegate.disconnect()
    }

    override fun setRequestMethod(method: String?) {
        metric.setMethod(method)
        delegate.requestMethod = method
    }

    override fun getResponseCode(): Int {
        return delegate.responseCode
    }
}
