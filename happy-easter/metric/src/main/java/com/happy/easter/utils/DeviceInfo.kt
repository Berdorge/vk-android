package com.happy.easter.utils

import android.os.Build

internal fun getDeviceName() = "${Build.MANUFACTURER} ${Build.MODEL}"
