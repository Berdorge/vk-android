package com.happy.easter.utils

import android.content.Context

internal fun Context.getVersionName(): String = packageManager.getPackageInfo(packageName, 0)
    .versionName
