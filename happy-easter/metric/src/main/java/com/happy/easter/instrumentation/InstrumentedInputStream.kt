package com.happy.easter.instrumentation

import java.io.InputStream

internal class InstrumentedInputStream(
    private val delegate: InputStream,
    private val stop: (responseBytes: Long) -> Unit
) : InputStream() {
    private var bytesRead = 0L

    override fun read(): Int {
        return delegate.read()
    }

    override fun read(b: ByteArray?): Int {
        return delegate.read(b)
    }

    override fun read(b: ByteArray?, off: Int, len: Int): Int {
        val bytesRead = delegate.read(b, off, len)
        if (bytesRead > 0) {
            this.bytesRead += bytesRead
        } else if (bytesRead == -1) {
            stop(this.bytesRead)
        }
        return bytesRead
    }

    override fun skip(n: Long): Long {
        return delegate.skip(n)
    }

    override fun available(): Int {
        return delegate.available()
    }

    override fun reset() {
        delegate.reset()
    }

    override fun close() {
        delegate.close()
        stop(this.bytesRead)
    }

    override fun mark(readlimit: Int) {
        delegate.mark(readlimit)
    }

    override fun markSupported(): Boolean {
        return delegate.markSupported()
    }
}
