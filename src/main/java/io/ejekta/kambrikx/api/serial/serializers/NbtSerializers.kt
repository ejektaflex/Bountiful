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
