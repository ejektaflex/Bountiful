package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.internal.NamedValueDecoder
import kotlinx.serialization.internal.NamedValueEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
@ExperimentalSerializationApi
class TagEncoder : NamedValueEncoder() {

    var root = CompoundTag()

    override fun encodeTaggedInt(tag: String, value: Int) {
        root.putInt(tag, value)
    }

    override fun encodeTaggedString(tag: String, value: String) {
        root.putString(tag, value)
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        root.putBoolean(tag, value)
    }

    override fun encodeTaggedDouble(tag: String, value: Double) {
        root.putDouble(tag, value)
    }

    override fun encodeTaggedByte(tag: String, value: Byte) {
        root.putByte(tag, value)
    }

    override fun encodeTaggedChar(tag: String, value: Char) {
        root.putString(tag, value.toString())
    }

    override fun encodeTaggedFloat(tag: String, value: Float) {
        root.putFloat(tag, value)
    }

    override fun encodeTaggedLong(tag: String, value: Long) {
        root.putLong(tag, value)
    }

    override fun encodeTaggedShort(tag: String, value: Short) {
        root.putShort(tag, value)
    }

    companion object {

    }

}