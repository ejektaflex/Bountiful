package io.ejekta.kambrikx.api.serial.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.util.Identifier

@OptIn(ExperimentalSerializationApi::class)
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




