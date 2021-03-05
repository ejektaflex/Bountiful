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

    // Currently, primitive encodings directly call addTag("PRIMITIVE", tag)
    override fun addTag(name: String?, tag: Tag) {
        root = tag
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
        //println("Class encoding '$name', $tag")
        root.put(name, tag)
    }
}

@InternalSerializationApi
class TagListEncoder(config: NbtFormatConfig, onEnd: (Tag) -> Unit) : BaseTagEncoder(config, onEnd) {
    override val root = ListTag()
    override fun addTag(name: String?, tag: Tag) {
        if (name != config.classDiscriminator) {
            root.add(name!!.toInt(), tag)
        }
    }
}

@InternalSerializationApi
@ExperimentalSerializationApi
class TagMapEncoder(config: NbtFormatConfig, onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(config, onEnd) {
    override var root = CompoundTag()
    private var theKey = ""
    private var isKey = true

    override fun addTag(name: String?, tag: Tag) {
        //println("Boop $name '$theKey' $tag (${tag::class})")
        if (name != config.classDiscriminator) {

            if (isKey) {
                theKey = tag.asString() ?: "DEFAULT_KEY"
            } else {
                root.put(theKey, tag)
            }

            isKey = !isKey

        }

    }
}