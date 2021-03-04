package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import net.minecraft.nbt.*

@InternalSerializationApi
@ExperimentalSerializationApi
class TagMapEncoder(onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(onEnd) {

    override var root = CompoundTag()

    private var theKey = ""

    private fun String?.isKey() = (this?.toInt() ?: 0) % 2 == 0

    override fun addTag(name: String?, tag: Tag) {
        if (name.isKey()) {
            theKey = tag.asString() ?: "DEFAULT_KEY"
        } else {
            root.put(theKey, tag)
        }
    }

    override fun encodeTaggedString(tag: String, value: String) {
        addTag(tag, StringTag.of(value))
    }

    override fun encodeTaggedInt(tag: String, value: Int) {
        addTag(tag, IntTag.of(value))
    }

    override fun encodeTaggedBoolean(tag: String, value: Boolean) {
        addTag(tag, ByteTag.of(value))
    }

}