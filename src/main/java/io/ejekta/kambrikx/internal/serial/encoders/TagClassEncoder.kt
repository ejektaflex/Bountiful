package io.ejekta.kambrikx.internal.serial.encoders

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import net.minecraft.nbt.*

@InternalSerializationApi
@ExperimentalSerializationApi
open class TagClassEncoder(onEnd: (Tag) -> Unit = {}) : BaseTagEncoder(onEnd) {

    override var root = CompoundTag()

    override fun addTag(name: String?, tag: Tag) {
        root.put(name, tag)
    }

}