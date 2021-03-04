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

    @ExperimentalSerializationApi
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        println("Tag is of kind: ${descriptor.kind} for curr: $currentTagOrNull in ctx: \t\t${this::class.simpleName}")
        return this
    }

    @ExperimentalSerializationApi
    override fun endEncode(descriptor: SerialDescriptor) {
        super.endEncode(descriptor)
        root.onEnd()
    }

}