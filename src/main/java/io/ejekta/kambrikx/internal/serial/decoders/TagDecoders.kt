package io.ejekta.kambrikx.internal.serial.decoders

import io.ejekta.kambrikx.api.serial.nbt.NbtFormatConfig
import io.ejekta.kambrikx.internal.serial.encoders.BaseTagEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagClassEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagListEncoder
import io.ejekta.kambrikx.internal.serial.encoders.TagMapEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
open class TagClassDecoder(
    config: NbtFormatConfig,
    level: Int,
    final override var root: CompoundTag
) : BaseTagDecoder(config, level) {
    private var position = 0

    override fun readTag(name: String): Tag {
        return root.get(name)!!
    }

    @ExperimentalSerializationApi
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (position < descriptor.elementsCount) {
            val name = descriptor.getTag(position++)
            if (name in root) {
                return position - 1
            }
        }
        return CompositeDecoder.DECODE_DONE
    }

}

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagMapDecoder(
    config: NbtFormatConfig,
    level: Int,
    final override var root: CompoundTag
) : BaseTagDecoder(config, level) {

    private val keys = root.keys.toList()
    private val size: Int = keys.size * 2
    private var position = -1

    override fun readTag(name: String): Tag {
        return root.get(name)!!
    }

    override fun elementName(desc: SerialDescriptor, index: Int): String {
        val i = index / 2
        return keys[i]
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (position < size - 1) {
            position++
            return position
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        // do nothing, maps do not have strict keys, so strict mode check is omitted
    }

}