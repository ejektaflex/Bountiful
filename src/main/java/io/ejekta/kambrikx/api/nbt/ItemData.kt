package io.ejekta.kambrikx.api.nbt

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrikx.api.serial.nbt.NbtFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import net.minecraft.item.ItemStack
import net.minecraft.nbt.Tag
import net.minecraft.util.Identifier

abstract class ItemData<T> {

    abstract val identifier: Identifier

    abstract val ser: KSerializer<T>

    abstract val default: () -> T

    private val defaultTag: Tag
        get() = format.encodeToTag(ser, default())

    open val format: NbtFormat = NbtFormat

    fun of(stack: ItemStack) = get(stack)

    private fun getSubtag(stack: ItemStack): Tag {
        stack.orCreateTag.apply {
            val key = identifier.toString()
            return if (key in this) {
                get(key)
            } else {
                put(key, defaultTag)
            }!!
        }
    }

    private fun setSubtag(stack: ItemStack, tag: Tag) {
        stack.orCreateTag.apply {
            put(identifier.toString(), tag)
        }
    }

    operator fun get(stack: ItemStack): T {
        val tag = getSubtag(stack)
        return try {
            format.decodeFromTag(ser, tag)
        } catch (e: SerializationException) {
            Kambrik.Logger.error("Failed to decode leaf '$identifier' in stack $stack (type: ${stack.item::class.simpleName})")
            default().also { set(stack, it) }
        }
    }

    operator fun set(stack: ItemStack, value: T) {
        val tag = format.encodeToTag(ser, value)
        setSubtag(stack, tag)
    }

    fun edit(stack: ItemStack, func: T.() -> Unit) {
        get(stack).apply(func).also { set(stack, it) }
    }

}