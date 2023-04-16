package com.happy.easter.utils

internal fun stackTraceFeature(): String = Thread.currentThread()
    .stackTrace
    .asSequence()
    .drop(2)
    .dropWhile {
        it.className.startsWith("com.happy.easter")
    }
    .joinToString("\n\tat ")
