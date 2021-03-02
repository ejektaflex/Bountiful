package io.ejekta.kambrikx.api.serial.nbt

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.internal.NamedValueEncoder
import net.minecraft.nbt.Tag

@InternalSerializationApi
abstract class BaseTagEncoder : NamedValueEncoder() {
    abstract val root: Tag
}