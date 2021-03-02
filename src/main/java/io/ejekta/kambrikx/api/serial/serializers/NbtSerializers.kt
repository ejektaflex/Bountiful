@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrik.ext.iterator
import io.ejekta.kambrikx.ext.internal.doCollection
import io.ejekta.kambrikx.ext.internal.doStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag


@Serializer(forClass = StringTag::class)
object StringTagSer : KSerializer<StringTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StringTaggy", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: StringTag) = encoder.doStructure(descriptor) {
        println("Serializing string tag: $value")
    }
    override fun deserialize(decoder: Decoder): StringTag = StringTag.of(decoder.decodeString())
}

@Serializer(forClass = CompoundTag::class)
object CompoundTagSer : KSerializer<CompoundTag> {
    override val descriptor: SerialDescriptor = mapSerialDescriptor<String, Tag>()
    override fun serialize(encoder: Encoder, value: CompoundTag) = encoder.doCollection(descriptor, value.size) {
        var ind = 0
        for ((key, tag) in value) {
            val next = ind++

            println("About to try to serialize '$key' with value $tag")

            when (tag) {
                is StringTag -> encodeSerializableElement(descriptor, next, StringTagSer, tag)
            }


        }
    }
    override fun deserialize(decoder: Decoder): CompoundTag {
        return CompoundTag().apply { putString("no", "u") }
    }
}