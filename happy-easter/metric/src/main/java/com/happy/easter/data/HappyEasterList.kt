package com.happy.easter.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class HappyEasterList(
    val list: List<HappyEasterMetricData>
)
