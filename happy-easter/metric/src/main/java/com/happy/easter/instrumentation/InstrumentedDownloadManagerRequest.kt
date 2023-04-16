package com.happy.easter.instrumentation

import android.app.DownloadManager
import android.net.Uri

internal class InstrumentedDownloadManagerRequest(
    val uri: Uri
) : DownloadManager.Request(uri)
