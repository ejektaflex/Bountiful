@file:Suppress("EXPERIMENTAL_API_USAGE")

package io.ejekta.kambrikx.api.serial.serializers

import io.ejekta.kambrik.ext.toTag
import io.ejekta.kambrikx.ext.internal.doStructure
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.nbt.Tag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box


@Serializer(forClass = Tag::class)
object TagSerializer : KSerializer<Tag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TagSerializer", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: Tag) { encoder.encodeString(value.asString()) }
    override fun deserialize(decoder: Decoder): Tag = decoder.decodeString().toTag()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(): KSerializer<T> {
        return TagSerializer as KSerializer<T>
    }
}

@Serializer(forClass = BlockPos::class)
object BlockPosSerializer : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.BlockPos") {
        element<Int>("x")
        element<Int>("y")
        element<Int>("z")
    }

    override fun serialize(encoder: Encoder, value: BlockPos) {
        encoder.doStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.x)
            encodeIntElement(descriptor, 1, value.y)
            encodeIntElement(descriptor, 2, value.z)
        }
    }

    override fun deserialize(decoder: Decoder): BlockPos {
        return decoder.doStructure(descriptor) {
            val x = decodeIntElement(descriptor, 0)
            val y = decodeIntElement(descriptor, 1)
            val z = decodeIntElement(descriptor, 2)
            BlockPos(x, y, z)
        }
    }
}

@Serializer(forClass = Box::class)
object BoxSerializer : KSerializer<Box> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("yarn.Box") {
        element<Double>("ax")
        element<Double>("ay")
        element<Double>("az")
        element<Double>("bx")
        element<Double>("by")
        element<Double>("bz")
    }

    override fun serialize(encoder: Encoder, value: Box) {
        encoder.doStructure(descriptor) {
            encodeDoubleElement(descriptor, 0, value.minX)
            encodeDoubleElement(descriptor, 1, value.minY)
            encodeDoubleElement(descriptor, 2, value.minZ)
            encodeDoubleElement(descriptor, 3, value.maxX)
            encodeDoubleElement(descriptor, 4, value.maxY)
            encodeDoubleElement(descriptor, 5, value.maxZ)
        }
    }

    override fun deserialize(decoder: Decoder): Box {
        return decoder.doStructure(descriptor) {
            val ax = decodeDoubleElement(descriptor, 0)
            val ay = decodeDoubleElement(descriptor, 1)
            val az = decodeDoubleElement(descriptor, 2)
            val bx = decodeDoubleElement(descriptor, 3)
            val by = decodeDoubleElement(descriptor, 4)
            val bz = decodeDoubleElement(descriptor, 5)
            Box(ax, ay, az, bx, by, bz)
        }
    }
}

object BlockPosSerializerOptimized : KSerializer<BlockPos> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("yarn.BlockPosOptimized", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: BlockPos) = encoder.encodeLong(value.asLong())
    override fun deserialize(decoder: Decoder): BlockPos { return BlockPos.fromLong(decoder.decodeLong()) }
}
