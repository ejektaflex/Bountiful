package io.ejekta.kambrik.ext.internal

import io.ejekta.kambrikx.serial.TagType
import net.minecraft.nbt.Tag
import java.nio.file.Path

internal val Path.assured: Path
    get() = also {
        it.toFile().apply {
            mkdirs()
        }
    }

internal val Tag.tagType: TagType
    get() = TagType.values().first { this.reader == it.reader }