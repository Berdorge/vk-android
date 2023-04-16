package com.happy.easter

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import org.gradle.api.Project

class HappyEasterTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return HappyEasterTransform::class.java.simpleName
    }

    private val typeClasses = setOf(QualifiedContent.DefaultContentType.CLASSES)

    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        return typeClasses
    }


    private val scopes = setOf(
        QualifiedContent.Scope.PROJECT,
        QualifiedContent.Scope.SUB_PROJECTS,
        QualifiedContent.Scope.EXTERNAL_LIBRARIES
    )

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return scopes.toMutableSet()
    }


    override fun isIncremental(): Boolean {
        return true
    }


    override fun transform(transformInvocation: TransformInvocation) {
        val appExtension = project.extensions.findByName("android") as AppExtension
        val ignores = listOf(Regex("com/happy/easter/.*"))
        val config = HappyEasterTransformConfig(transformInvocation, appExtension.bootClasspath, ignores)
        HappyEasterTransformImpl(config).transform()
    }
}
