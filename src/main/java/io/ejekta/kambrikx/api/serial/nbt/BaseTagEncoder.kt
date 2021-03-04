package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.NamedValueEncoder
import net.minecraft.nbt.Tag

@InternalSerializationApi
abstract class BaseTagEncoder(open val onEnd: Tag.() -> Unit = {}) : NamedValueEncoder() {

    abstract val root: Tag

    abstract fun addTag(name: String?, tag: Tag)

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        super.beginStructure(descriptor)
        //println(descriptor)
        return when (descriptor.kind) {
            StructureKind.LIST -> TagListTypeEncoder {
                addTag(currentTagOrNull, it)
            }
            StructureKind.CLASS -> TagClassEncoder {
                addTag(currentTagOrNull, it)
            }
            StructureKind.MAP -> TagMapEncoder {
                addTag(currentTagOrNull, it)
            }
            else -> throw Exception("Could not begin ! Was a: ${descriptor.kind}")
        }
    }

    @ExperimentalSerializationApi
    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        root.onEnd()
    }

}