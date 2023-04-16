package com.happy.easter

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformInvocation
import com.happy.easter.asm.HappyEasterClassInstrumenter
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.Locale
import java.util.jar.JarFile

class HappyEasterTransformImpl(config: HappyEasterTransformConfig) {
    private val transformInvocation = config.transformInvocation
    private val androidClasspath = config.androidClasspath
    private val ignorePaths = config.ignorePaths
    private val outputProvider = transformInvocation.outputProvider
    private val instrumentationConfig = HappyEasterInstrumentationConfig(
        buildRuntimeClasspath(transformInvocation)
    )
    private val instrumenter = HappyEasterClassInstrumenter(instrumentationConfig)

    fun transform() {
        for (input in transformInvocation.inputs) {
            instrumentDirectoryInputs(input.directoryInputs)
            instrumentJarInputs(input.jarInputs)
        }
    }

    private fun buildRuntimeClasspath(transformInvocation: TransformInvocation): List<URL> {
        val allTransformInputs = transformInvocation.inputs + transformInvocation.referencedInputs
        val allJarsAndDirs = allTransformInputs.map { input ->
            (input.directoryInputs + input.jarInputs).map { it.file }
        }
        val allClassesAtRuntime = androidClasspath + allJarsAndDirs.flatten()
        return allClassesAtRuntime.map {
            it.toURI().toURL()
        }
    }


    private fun instrumentDirectoryInputs(directoryInputs: Collection<DirectoryInput>) {
        for (input in directoryInputs) {
            val outDir = outputProvider.getContentLocation(
                input.name,
                input.contentTypes,
                input.scopes,
                Format.DIRECTORY
            )
            if (transformInvocation.isIncremental) {
                // Incremental builds will specify which individual class files changed.
                for (changedFile in input.changedFiles) {
                    when (changedFile.value) {
                        Status.ADDED, Status.CHANGED -> {
                            val relativeFile =
                                normalizedRelativeFilePath(input.file, changedFile.key)
                            val destFile = File(outDir, relativeFile)
                            changedFile.key.inputStream().use { inputStream ->
                                destFile.outputStream().use { outputStream ->
                                    if (isInstrumentableClassFile(relativeFile)) {
                                        processClassStream(inputStream, outputStream)
                                    } else {
                                        copyStream(inputStream, outputStream)
                                    }
                                }
                            }
                        }
                        Status.REMOVED -> {
                            val relativeFile =
                                normalizedRelativeFilePath(input.file, changedFile.key)
                            val destFile = File(outDir, relativeFile)
                            FileUtils.forceDelete(destFile)
                        }
                        Status.NOTCHANGED, null -> {
                        }
                    }
                }
            } else {
                ensureDirectoryExists(outDir)
                FileUtils.cleanDirectory(outDir)
                var count = 0
                for (file in FileUtils.iterateFiles(input.file, null, true)) {
                    val relativeFile = normalizedRelativeFilePath(input.file, file)
                    val destFile = File(outDir, relativeFile)
                    ensureDirectoryExists(destFile.parentFile)
                    IOUtils.buffer(file.inputStream()).use { inputStream ->
                        IOUtils.buffer(destFile.outputStream()).use { outputStream ->
                            if (isInstrumentableClassFile(relativeFile)) {
                                try {
                                    processClassStream(inputStream, outputStream)
                                } catch (e: Exception) {
                                    throw e
                                }
                            } else {
                                copyStream(inputStream, outputStream)
                            }
                        }
                    }
                    count++
                }
            }
        }
    }

    private fun instrumentJarInputs(jarInputs: Collection<JarInput>) {
        for (input in jarInputs) {
            val outDir = outputProvider.getContentLocation(
                input.name,
                input.contentTypes,
                input.scopes,
                Format.DIRECTORY
            )
            val doTransform =
                !transformInvocation.isIncremental || input.status == Status.ADDED || input.status == Status.CHANGED
            if (doTransform) {
                ensureDirectoryExists(outDir)
                FileUtils.cleanDirectory(outDir)
                val inJar = JarFile(input.file)
                var count = 0
                for (entry in inJar.entries()) {
                    val outFile = File(outDir, entry.name)
                    if (!entry.isDirectory) {
                        ensureDirectoryExists(outFile.parentFile)
                        inJar.getInputStream(entry).use { inputStream ->
                            IOUtils.buffer(FileOutputStream(outFile)).use { outputStream ->
                                if (isInstrumentableClassFile(entry.name)) {
                                    try {
                                        processClassStream(inputStream, outputStream)
                                    } catch (e: Exception) {
                                        throw e
                                    }
                                } else {
                                    copyStream(inputStream, outputStream)
                                }
                            }
                        }
                        count++
                    }
                }
            } else if (input.status == Status.REMOVED) {
                if (outDir.exists()) {
                    FileUtils.forceDelete(outDir)
                }
            }
        }
    }

    private fun ensureDirectoryExists(dir: File) {
        if (!((dir.isDirectory && dir.canWrite()) || dir.mkdirs())) {
            throw IOException("Can't write or create ${dir.path}")
        }
    }

    private fun normalizedRelativeFilePath(parent: File, file: File): String {
        val parts = mutableListOf<String>()
        var current = file
        while (current != parent) {
            parts.add(current.name)
            current = current.parentFile
        }
        return parts.asReversed().joinToString("/")
    }

    private fun isInstrumentableClassFile(path: String): Boolean {
        return if (ignorePaths.any { it.matches(path) }) {
            false
        } else {
            path.lowercase(Locale.getDefault()).endsWith(".class")
        }
    }

    private fun copyStream(inputStream: InputStream, outputStream: OutputStream) {
        IOUtils.copy(inputStream, outputStream)
    }

    private fun processClassStream(
        inputStream: InputStream,
        outputStream: OutputStream
    ) {
        val classBytes = IOUtils.toByteArray(inputStream)
        val bytesToWrite = try {
            val instrBytes = instrumenter.instrument(classBytes)
            instrBytes
        } catch (e: Exception) {
            classBytes
        }
        IOUtils.write(bytesToWrite, outputStream)
    }
}
