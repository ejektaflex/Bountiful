package io.ejekta.kambrik.api.file

import kotlinx.serialization.SerializationStrategy
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class KambrikFileApi internal constructor() {

    fun getBaseFolder(modId: String): Path {
        return Paths.get("config", modId)
    }

}