package com.happy.easter

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class HappyEasterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.findByName("android")
        if (ext != null && ext is AppExtension) {
            ext.registerTransform(HappyEasterTransform(project))
        } else {
            throw Exception("${HappyEasterPlugin::class.java.name} plugin may only be applied to Android app projects")
        }
    }
}
