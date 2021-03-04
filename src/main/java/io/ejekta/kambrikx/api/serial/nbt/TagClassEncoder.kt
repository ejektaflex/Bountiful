package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
@ExperimentalSerializationApi
class TagClassEncoder(onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(onEnd) {

    override var root = CompoundTag()

    override fun addTag(name: String?, tag: Tag) {
        root.put(name, tag)
    }

    // Why would I ever want to compose these names together? Leave only base name
    override fun composeName(parentName: String, childName: String): String {
        return childName
    }

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

}