package com.happy.easter.asm

import com.happy.easter.HappyEasterInstrumentationConfig
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.net.URLClassLoader

class HappyEasterClassInstrumenter(config: HappyEasterInstrumentationConfig) {
    private val classLoader = URLClassLoader(config.runtimeClasspath.toTypedArray())

    fun instrument(input: ByteArray): ByteArray {
        val classReader = ClassReader(input)

        val classWriter = object : ClassWriter(COMPUTE_MAXS or COMPUTE_FRAMES) {
            override fun getClassLoader(): ClassLoader = this@HappyEasterClassInstrumenter.classLoader
        }

        val classVisitor = HappyEasterClassVisitor(classWriter)
        classReader.accept(classVisitor, ClassReader.SKIP_FRAMES)

        return classWriter.toByteArray()
    }
}
