package io.ejekta.kambrikx.internal.serial.encoders

import kotlinx.serialization.InternalSerializationApi
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagListTypeEncoder(onEnd: (Tag) -> Unit) : BaseTagEncoder(onEnd) {

    override val root = ListTag()

    override fun addTag(name: String?, tag: Tag) {
        root.add(tag)
    }

}