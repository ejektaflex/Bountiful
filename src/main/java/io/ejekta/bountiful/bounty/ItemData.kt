package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.config.Format
import io.ejekta.kambrikx.ext.toStrictTag
import io.ejekta.kambrikx.api.nbt.TagConverterStrict
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier

abstract class ItemData<T> {

    abstract val identifier: Identifier

    abstract val ser: KSerializer<T>

    abstract val creator: () -> T

    open val format: NbtFormat = NbtFormat

    fun getDataLeaf(stack: ItemStack): Tag {
        stack.orCreateTag.apply {
            val key = identifier.toString()
            return if (key in this) {
                get(key)
            } else {
                val defaultTag = format.encodeToTag(ser, creator())
                put(key, defaultTag)
            }!!
        }
    }

    fun setDataLeaf(stack: ItemStack, tag: Tag) {
        stack.orCreateTag.apply {
            put(identifier.toString(), tag)
        }
    }

    operator fun get(stack: ItemStack) : T {
        val tag = getDataLeaf(stack)
        return format.decodeFromTag(ser, tag)
    }

    operator fun set(stack: ItemStack, value: T) {
        val tag = format.encodeToTag(ser, value)
        setDataLeaf(stack, tag)
    }

    fun edit(stack: ItemStack, func: T.() -> Unit) {
        get(stack).apply(func).also { set(stack, it) }
    }

}