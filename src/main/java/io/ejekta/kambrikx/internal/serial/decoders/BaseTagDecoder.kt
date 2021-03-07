package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.NamedValueDecoder
import net.minecraft.nbt.*

@InternalSerializationApi
abstract class BaseTagDecoder(
    @JvmField protected val config: NbtFormatConfig,
    var level: Int = 0
) : NamedValueDecoder() {

    abstract val root: Tag

    private fun currentTag(): Tag = currentTagOrNull?.let { readTag(it) } ?: root

    open fun readTag(name: String): Tag {
        return root
    }

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        config.logInfo(level, "Parse: ${descriptor.kind}")
        return when (descriptor.kind) {
            StructureKind.CLASS -> TagClassDecoder(config, level + 1, currentTag())
            StructureKind.LIST -> TagListDecoder(config, level + 1, currentTag())
            StructureKind.MAP -> TagMapDecoder(config, level + 1, currentTag())
            else -> throw Exception("Cannot decode a ${descriptor.kind} yet with beginStructure!")
        }
    }

    override fun decodeTaggedInt(tag: String): Int = (readTag(tag) as IntTag).int
    override fun decodeTaggedString(tag: String): String {
        return (readTag(tag) as StringTag).asString()
    }
    override fun decodeTaggedBoolean(tag: String): Boolean = (readTag(tag) as ByteTag).byte > 0
    override fun decodeTaggedDouble(tag: String): Double = (readTag(tag) as DoubleTag).double
    override fun decodeTaggedByte(tag: String): Byte = (readTag(tag) as ByteTag).byte
    override fun decodeTaggedChar(tag: String): Char = (readTag(tag) as StringTag).asString().first()
    override fun decodeTaggedFloat(tag: String): Float = (readTag(tag) as FloatTag).float
    override fun decodeTaggedLong(tag: String): Long = (readTag(tag) as LongTag).long
    override fun decodeTaggedShort(tag: String): Short = (readTag(tag) as ShortTag).short


}