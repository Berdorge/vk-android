package com.happy.easter.instrumentation

import java.net.HttpURLConnection
import java.net.URL

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class InstrumentedHttpUrlConnection(
    url: URL,
    delegate: HttpURLConnection
) : InstrumentedUrlConnection by BaseInstrumentedHttpUrlConnection(url, delegate),
    HttpURLConnection(url)
