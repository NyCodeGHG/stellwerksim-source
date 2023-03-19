#!/usr/bin/env kotlin

@file:Repository("https://maven.quiltmc.org/repository/release/", "https://repo1.maven.org/maven2/")
@file:DependsOn(
    "org.jetbrains.kotlin:kotlin-stdlib:1.8.10",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta",
    "org.quiltmc:quiltflower:1.9.0"
)
@file:OptIn(ExperimentalPathApi::class, ExperimentalTime::class)

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.java.decompiler.main.Fernflower
import org.jetbrains.java.decompiler.main.decompiler.DirectoryResultSaver
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.util.ZipFileCache
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.file.Path
import java.util.zip.ZipOutputStream
import kotlin.io.path.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

val httpClient = HttpClient.newHttpClient()

fun downloadJar(name: String) {
    val request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create("https://www.stellwerksim.de/$name"))
        .build()
    httpClient.send(request, BodyHandlers.ofFile(Path("temp") / name))
}

fun createTempDir() {
    val dir = Path("temp")
    if (dir.exists()) {
        dir.deleteRecursively()
    }
    dir.createDirectory()
}

createTempDir()

data class Jar(val jarName: String, val moduleName: String)

val SKIP_EXTENSIONS_IN_RESOURCES = arrayOf("java", "jar", "LIST", "RSA", "SF")

runBlocking {
    coroutineScope {
        val jars = arrayOf(
            Jar("STSQuery.jar", "query"),
            Jar("sts.jar", "sts"),
            Jar("js-tools.jar", "sts")
        )

        // Create Source Directories for each module
        for (module in jars.map { it.moduleName }.distinct()) {
            createSourceDirectories(module)
        }

        for (jar in jars) {
            launch(Dispatchers.IO) {
                @OptIn(ExperimentalTime::class)
                val time = measureTime {
                    println("Downloading $jar")
                    downloadJar(jar.jarName)
                }
                println("Downloaded $jar ($time)")
                val decompilePath = Path("temp") / "decompile_${jar.jarName.replace(".jar", "")}"
                with(decompilePath) {
                    deleteRecursively()
                    createDirectories()
                }

                val decompiler = Decompiler(decompilePath)
                decompiler.addSource(Path("temp") / jar.jarName)
                decompiler.decompileContext()

                val javaModulePath = Path(jar.moduleName, "src", "main", "java")
                copyFiles(
                    decompilePath.traverse()
                        .filter { it.extension == "java" || it.isDirectory() },
                    javaModulePath,
                    decompilePath
                )

                val resourcesModulePath = Path(jar.moduleName, "src", "main", "resources")
                copyFiles(
                    decompilePath.traverse()
                        .filter { it.extension !in SKIP_EXTENSIONS_IN_RESOURCES || it.isDirectory() },
                    resourcesModulePath,
                    decompilePath
                )
            }
        }
    }
}

fun copyFiles(files: Sequence<Path>, rootDir: Path, decompilePath: Path) {
    for (entry in files) {
        val destination = rootDir / decompilePath.relativize(entry)
        if (entry.isRegularFile()) {
            destination.parent.createDirectories()
            if (destination.exists()) {
                error("$destination already exists.")
            }
            println("Copying $entry to $destination")
            entry.copyTo(destination)
        }
    }
}

fun Path.traverse(): Sequence<Path> {
    return sequence {
        for (entry in listDirectoryEntries()) {
            yield(entry)
            if (entry.isDirectory()) {
                yieldAll(entry.traverse())
            }
        }
    }
}

@Suppress("NAME_SHADOWING")
fun createSourceDirectories(moduleName: String) {
    for (path in arrayOf("java", "resources")) {
        val path = Path(moduleName, "src", "main", path)
        if (path.exists()) {
            path.deleteRecursively()
        }
        path.createDirectories()
    }
}

@Suppress("NAME_SHADOWING")
class Decompiler(outDir: Path) : IFernflowerLogger() {
    private val engine: Fernflower =
        Fernflower(DirectoryResultSaver(outDir.toFile()), mapOf("hes" to 1), this)
    private val mapArchiveStreams = HashMap<String, ZipOutputStream>()
    private val mapArchiveEntries = HashMap<String, MutableSet<String>>()
    private val openZips = ZipFileCache()

    fun addSource(source: Path) {
        engine.addSource(source.toFile())
    }

    fun decompileContext() {
        try {
            engine.decompileContext()
        } finally {
            engine.clearContext()
        }
    }

    override fun writeMessage(message: String, severity: Severity) {
        println("${severity.prefix} $message")
    }

    override fun writeMessage(message: String, severity: Severity, exception: Throwable) {
        println("${severity.prefix} $message")
        exception.printStackTrace()
    }
}
