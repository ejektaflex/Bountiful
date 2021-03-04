package io.ejekta.kambrikx.internal.serial.encoders

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagEncoder : BaseTagEncoder() {
    override var root: Tag = EndTag.INSTANCE

    override fun addTag(name: String?, tag: Tag) {
        throw Exception("Cannot add tag in base encoder!")
    }

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        val ending: (Tag) -> Unit = { root = it }
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListEncoder(ending)
            StructureKind.CLASS -> TagClassEncoder(ending)
            StructureKind.MAP -> TagMapEncoder(ending)
            else -> super.beginStructure(descriptor)
        }
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagClassEncoder(onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(onEnd) {
    override var root = CompoundTag()
    override fun addTag(name: String?, tag: Tag) {
        root.put(name, tag)
    }
}

@InternalSerializationApi
class TagListEncoder(onEnd: (Tag) -> Unit) : BaseTagEncoder(onEnd) {
    override val root = ListTag()
    override fun addTag(name: String?, tag: Tag) {
        root.add(tag)
    }
}

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
}