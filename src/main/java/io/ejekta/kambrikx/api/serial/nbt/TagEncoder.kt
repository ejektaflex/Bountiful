package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagEncoder(onEnd: (Tag) -> Unit = { }) : BaseTagEncoder(onEnd) {

    override var root: Tag = EndTag.INSTANCE

    override fun addTag(name: String, tag: Tag) {
        throw Exception("Cannot add tag in base encoder!")
    }

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListTypeEncoder {
                root = it
            }
            StructureKind.CLASS -> TagClassEncoder(onEnd)
            else -> super.beginStructure(descriptor)
        }
    }

}