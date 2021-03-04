package io.ejekta.kambrikx.internal.serial.encoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagEncoder(config: NbtFormatConfig) : BaseTagEncoder(config) {
    override var root: Tag = EndTag.INSTANCE

    override fun addTag(name: String?, tag: Tag) {
        throw Exception("Cannot add tag in base encoder!")
    }

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        val ending: (Tag) -> Unit = { root = it }
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListEncoder(config, ending)
            StructureKind.CLASS -> TagClassEncoder(config, ending)
            StructureKind.MAP -> TagMapEncoder(config, ending)
            else -> super.beginStructure(descriptor)
        }
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagClassEncoder(config: NbtFormatConfig, onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(config, onEnd) {
    override var root = CompoundTag()
    override fun addTag(name: String?, tag: Tag) {
        root.put(name, tag)
    }
}

@InternalSerializationApi
class TagListEncoder(config: NbtFormatConfig, onEnd: (Tag) -> Unit) : BaseTagEncoder(config, onEnd) {
    override val root = ListTag()
    override fun addTag(name: String?, tag: Tag) {
        root.add(name!!.toInt(), tag)
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
class TagMapEncoder(config: NbtFormatConfig, onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(config, onEnd) {
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