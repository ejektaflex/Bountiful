package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrikx.api.nbt.NbtMode
import io.ejekta.kambrikx.ext.toJson
import io.ejekta.kambrikx.ext.toStrictJson
import io.ejekta.kambrikx.ext.toStrictTag
import io.ejekta.kambrikx.ext.toTag
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier

@ExperimentalSerializationApi
@Serializer(forClass = Identifier::class)
object IdentitySer : KSerializer<Identifier> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifier", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Identifier) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): Identifier {
        return Identifier(decoder.decodeString())
    }
}

@ExperimentalSerializationApi
@Serializer(forClass = CompoundTag::class)
object NbtTagSer : KSerializer<CompoundTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TestDescriptor", PrimitiveKind.STRING)
    val mode = NbtMode.STRICT
    override fun serialize(encoder: Encoder, value: CompoundTag) {
        encoder.encodeSerializableValue(JsonObject.serializer(), value.toJson(mode).jsonObject)
    }
    override fun deserialize(decoder: Decoder): CompoundTag {
        return decoder.decodeSerializableValue(JsonObject.serializer()).toTag(mode) as CompoundTag
    }
}

