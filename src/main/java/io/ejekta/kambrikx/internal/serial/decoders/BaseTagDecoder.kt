package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
abstract class BaseTagDecoder(
    @JvmField protected val config: NbtFormatConfig,
    var level: Int = 0
) : NamedValueDecoder() {

    abstract val root: Tag

    abstract fun readTag(name: String): Tag

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        config.logInfo(level, "Parse: ${descriptor.kind}")
        return when (descriptor.kind) {
            StructureKind.CLASS -> TagClassDecoder(config, level + 1, root as CompoundTag)
            else -> throw Exception("Cannot decode a ${descriptor.kind} yet with beginStructure!")
        }
    }

    override fun decodeTaggedInt(tag: String): Int = (readTag(tag) as IntTag).int

    override fun decodeTaggedString(tag: String): String = (readTag(tag) as StringTag).asString()


}