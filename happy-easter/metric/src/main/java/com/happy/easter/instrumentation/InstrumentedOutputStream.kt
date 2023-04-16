package com.happy.easter.instrumentation

import com.happy.easter.HappyEasterHttpMetric
import java.io.OutputStream

internal class InstrumentedOutputStream(
    private val metric: HappyEasterHttpMetric,
    private val delegate: OutputStream
) : OutputStream() {
    private var bytesWritten = 0L

    override fun write(b: Int) {
        metric.setRequestBytes(++bytesWritten)
        delegate.write(b)
    }
}
