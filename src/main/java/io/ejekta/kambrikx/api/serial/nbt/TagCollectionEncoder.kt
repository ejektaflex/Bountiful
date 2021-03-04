package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.minecraft.nbt.*

@InternalSerializationApi
abstract class TagCollectionEncoder(override val root: Tag, onEnd: (Tag) -> Unit) : BaseTagEncoder(onEnd) {

    override fun encodeTaggedInt(tag: String, value: Int) {
        addTag(tag, IntTag.of(value))
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        addTag(tag, ByteTag.of(value))
    }

    override fun encodeTaggedChar(tag: String, value: Char) {
        addTag(tag, StringTag.of(value.toString()))
    }

    override fun encodeTaggedByte(tag: String, value: Byte) {
        addTag(tag, ByteTag.of(value))
    }

    override fun encodeTaggedLong(tag: String, value: Long) {
        addTag(tag, LongTag.of(value))
    }

    override fun encodeTaggedString(tag: String, value: String) {
        addTag(tag, StringTag.of(value))
    }

}