package com.happy.easter

import com.android.build.api.transform.TransformInvocation
import java.io.File

data class HappyEasterTransformConfig(
    val transformInvocation: TransformInvocation,
    val androidClasspath: List<File>,
    val ignorePaths: List<Regex>
)
