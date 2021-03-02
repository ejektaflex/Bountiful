package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagListTypeEncoder(val onEnd: Tag.() -> Unit) : BaseTagEncoder() {

    override val root = ListTag()

    private fun addTag(tag: Tag) {
        root.add(tag)
    }

    override fun encodeTaggedInt(tag: String, value: Int) {
        addTag(IntTag.of(value))
    }

    @ExperimentalSerializationApi
    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        root.onEnd()
    }

}