package io.ejekta.kambrikx.internal.serial.encoders

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.Tag

@InternalSerializationApi
class TagEncoder : BaseTagEncoder() {

    override var root: Tag = EndTag.INSTANCE

    override fun addTag(name: String?, tag: Tag) {
        throw Exception("Cannot add tag in base encoder!")
    }

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        val ending: (Tag) -> Unit = { root = it }
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListTypeEncoder(ending)
            StructureKind.CLASS -> TagClassEncoder(ending)
            else -> super.beginStructure(descriptor)
        }
    }

}