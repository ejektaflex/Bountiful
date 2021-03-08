package io.ejekta.kambrik.ext.internal

import java.nio.file.Path

internal val Path.assured: Path
    get() = also {
        it.toFile().apply {
            mkdirs()
        }
    }
