package io.ejekta.kambrik.api.file

import java.nio.file.Path
import java.nio.file.Paths

/**
 * Accessed via [Kambrik.File][io.ejekta.kambrik.Kambrik.File]
 */
class KambrikFileApi internal constructor() {

    fun getBaseFolder(modId: String): Path {
        return Paths.get("config", modId)
    }

}