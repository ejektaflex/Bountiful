package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrikx.ext.internal.doStructure
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

open class RegistryObjectSerializer<T>(private val reg: Registry<T>, serialName: String) : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.doStructure(ItemRefSerializer.descriptor) {
            encodeStringElement(ItemRefSerializer.descriptor, 0, reg.getId(value)?.toString()
                ?: throw SerializationException("Could not save identifier, as this object is not registered!: $value"))
        }
    }

    override fun deserialize(decoder: Decoder): T {
        return decoder.doStructure(ItemRefSerializer.descriptor) {
            val id = decodeStringElement(ItemRefSerializer.descriptor, 0)
            reg[Identifier(id)] ?: throw SerializationException("Could not find saved identifier!: $id")
        }
    }

}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Item::class)
object ItemRefSerializer : RegistryObjectSerializer<Item>(Registry.ITEM, "ref.yarn.Item")


