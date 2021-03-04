package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagListTypeEncoder(onEnd: (Tag) -> Unit) : TagCollectionEncoder(ListTag(), onEnd) {

    override val root = ListTag()

    override fun addTag(name: String, tag: Tag) {
        root.add(tag)
    }

}