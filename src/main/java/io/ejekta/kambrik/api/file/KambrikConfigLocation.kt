package io.ejekta.kambrik.api.file

import io.ejekta.bountiful.common.data.Pool
import io.ejekta.kambrik.ext.internal.assured
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class KambrikConfigLocation(private val modId: String) {

    val configFolder: Path
        get() = Paths.get("config", modId).assured

    fun getConfigFolder(path: Path): Path {
        return configFolder.resolve(path).assured
    }

    fun getConfigFolder(subfolder: String): Path {
        return configFolder.resolve(subfolder).assured
    }

    fun getConfigFile(path: Path, name: String): File {
        return configFolder.resolve(path).assured.resolve(name).toFile().apply {
            createNewFile()
        }
    }

    fun getConfigFile(name: String): File {
        return configFolder.resolve(name).toFile().apply {
            createNewFile()
        }
    }

    fun getConfigFiles(path: Path): List<File> {
        return configFolder.resolve(path).toFile().listFiles { dir, name -> dir.isFile && name.endsWith(".json") }?.toList() ?: listOf()
    }

    fun <T> writeConfig(
        file: File,
        data: T,
        serializer: KSerializer<T>,
        format: Json = DefaultConfigFormat
    ) {
        file.apply {
            if (!exists()) { createNewFile() }
            val text = format.encodeToString(serializer, data)
            writeText(text)
        }
    }

    fun <T> readConfig(
        file: File,
        serializer: KSerializer<T>,
        format: Json = DefaultConfigFormat
    ): T? {
        file.apply {
            if (!exists()) {
                return null
            }
            val text = readText()
            return format.decodeFromString(serializer, text)
        }
    }

    fun <T> editConfig(
        file: File,
        serializer: KSerializer<T>,
        default: T,
        format: Json = DefaultConfigFormat,
        func: T.() -> Unit,
    ) {
        val oldConfig = readConfig(file, serializer, format)
        val oldData = oldConfig ?: default
        oldData.apply(func)
        writeConfig(file, oldData, serializer, format)
    }

    companion object {
        val DefaultConfigFormat = Json {
            prettyPrint = true
        }
    }

}