package io.ejekta.kambrikx.internal.serial.encoders

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import net.minecraft.nbt.*

@InternalSerializationApi
abstract class BaseTagEncoder(open val onEnd: Tag.() -> Unit = {}) : NamedValueEncoder() {

    abstract val root: Tag

    abstract fun addTag(name: String?, tag: Tag)

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        //println(descriptor)
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListTypeEncoder { addTag(currentTagOrNull, it) }
            StructureKind.CLASS -> TagClassEncoder { addTag(currentTagOrNull, it) }
            StructureKind.MAP -> TagMapEncoder { addTag(currentTagOrNull, it) }
            else -> throw Exception("Could not begin ! Was a: ${descriptor.kind}")
        }
    }

    // Leave only base name
    override fun composeName(parentName: String, childName: String): String {
        return childName
    }

    @ExperimentalSerializationApi
    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        root.onEnd()
    }

    override fun encodeTaggedInt(tag: String, value: Int) {
        addTag(tag, IntTag.of(value))
    }

    override fun encodeTaggedString(tag: String, value: String) {
        addTag(tag, StringTag.of(value))
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        addTag(tag, ByteTag.of(value))
    }

    override fun encodeTaggedDouble(tag: String, value: Double) {
        addTag(tag, DoubleTag.of(value))
    }

    override fun encodeTaggedByte(tag: String, value: Byte) {
        addTag(tag, ByteTag.of(value))
    }

    override fun encodeTaggedChar(tag: String, value: Char) {
        addTag(tag, StringTag.of(value.toString()))
    }

    override fun encodeTaggedFloat(tag: String, value: Float) {
        addTag(tag, FloatTag.of(value))
    }

    override fun encodeTaggedLong(tag: String, value: Long) {
        addTag(tag, LongTag.of(value))
    }

    override fun encodeTaggedShort(tag: String, value: Short) {
        addTag(tag, ShortTag.of(value))
    }

}