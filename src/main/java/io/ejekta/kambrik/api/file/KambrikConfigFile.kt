package io.ejekta.kambrik.api.file

import io.ejekta.kambrik.ext.internal.assured
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

data class KambrikConfigFile<T>(val location: Path, val name: String, val format: Json = Json, val mode: KambrikReadFailMode, val serializer: KSerializer<T>, val default: () -> T) {

    private fun getOrCreateFile(): File {
        return location.assured.resolve(name).toFile().apply {
            if (!exists()) {
                createNewFile()
            }
        }
    }

    fun ensureExistence() {
        getOrCreateFile()
    }

    fun read(): T {
        return with(getOrCreateFile()) {
            val contents = readText()
            try {
                format.decodeFromString(serializer, contents)
            } catch (e: Exception) {
                println("Kambrik could not correctly load config data at: $location - $name, reason: ${e.message}")
                println("Kambrik is set to ${mode.name} this file data for safety")
                e.printStackTrace()

                if (mode == KambrikReadFailMode.OVERWRITE) {
                    write()
                }

                return default()
            }
        }
    }

    fun write(data: T? = null) {
        getOrCreateFile().run {
            val contents = format.encodeToString(serializer, data ?: default())
            writeText(contents)
        }
    }

    fun edit(func: T.() -> Unit) {
        write(read().apply(func))
    }

}