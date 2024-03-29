package com.happy.easter.instrumentation

import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.security.Permission

internal interface InstrumentedUrlConnection {
    fun getContentEncoding(): String?

    fun getHeaderField(name: String?): String?

    fun getReadTimeout(): Int

    fun connect()

    fun getUseCaches(): Boolean

    fun setConnectTimeout(timeout: Int)

    fun getDate(): Long

    fun getExpiration(): Long

    fun getContent(): Any

    fun getContent(classes: Array<out Class<Any>>?): Any

    fun getContentLengthLong(): Long

    fun getHeaderFieldInt(name: String?, Default: Int): Int

    fun setUseCaches(usecaches: Boolean)

    fun getIfModifiedSince(): Long

    fun setIfModifiedSince(ifmodifiedsince: Long)

    fun getDoInput(): Boolean

    fun getLastModified(): Long

    fun setDefaultUseCaches(defaultusecaches: Boolean)

    fun setDoOutput(dooutput: Boolean)

    fun getDefaultUseCaches(): Boolean

    fun getRequestProperties(): MutableMap<String, MutableList<String>>

    fun setReadTimeout(timeout: Int)

    fun getDoOutput(): Boolean

    fun addRequestProperty(key: String?, value: String?)

    fun getConnectTimeout(): Int

    fun setDoInput(doinput: Boolean)

    fun getHeaderFields(): MutableMap<String, MutableList<String>>

    fun getInputStream(): InputStream

    fun getAllowUserInteraction(): Boolean

    fun getURL(): URL

    fun setRequestProperty(key: String?, value: String?)

    fun setAllowUserInteraction(allowuserinteraction: Boolean)

    fun getContentLength(): Int

    fun getContentType(): String

    fun getRequestProperty(key: String?): String

    fun getOutputStream(): OutputStream

    fun getHeaderFieldLong(name: String?, Default: Long): Long

    fun getHeaderField(n: Int): String

    fun usingProxy(): Boolean

    fun getHeaderFieldKey(n: Int): String

    fun setInstanceFollowRedirects(followRedirects: Boolean)

    fun getHeaderFieldDate(name: String?, Default: Long): Long

    fun setChunkedStreamingMode(chunklen: Int)

    fun getPermission(): Permission

    fun getInstanceFollowRedirects(): Boolean

    fun getRequestMethod(): String

    fun getErrorStream(): InputStream

    fun getResponseMessage(): String

    fun setFixedLengthStreamingMode(contentLength: Int)

    fun setFixedLengthStreamingMode(contentLength: Long)

    fun disconnect()

    fun setRequestMethod(method: String?)

    fun getResponseCode(): Int
}
