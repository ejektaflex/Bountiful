package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.config.Format
import io.ejekta.kambrikx.serial.JsonStrict.toStrictTag
import io.ejekta.kambrikx.serial.convert.TagConverterStrict
import kotlinx.serialization.KSerializer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

abstract class ItemData<T> {

    abstract val ser: KSerializer<T>

    abstract val creator: () -> T

    operator fun get(stack: ItemStack) : T {
        return if (stack.hasTag()) {
            val data = TagConverterStrict.toJson(stack.tag!!)
            return try {
                Format.NBT.decodeFromJsonElement(ser, data)
            } catch (e: Exception) {
                setSafeData(stack)
            }
        } else {
            setSafeData(stack)
        }
    }

    fun getUnsafe(stack: ItemStack) : T {
        val data = TagConverterStrict.toJson(stack.tag!!)
        return Format.NBT.decodeFromJsonElement(ser, data)
    }

    operator fun set(stack: ItemStack, value: T) {
        stack.tag = Format.NBT.encodeToJsonElement(ser, value).toStrictTag() as CompoundTag
    }

    fun edit(stack: ItemStack, func: T.() -> Unit) {
        get(stack).apply(func).also { set(stack, it) }
    }

    fun setSafeData(stack: ItemStack): T {
        return creator().apply {
            stack.tag = Format.NBT.encodeToJsonElement(ser, this).toStrictTag() as CompoundTag
        }
    }

}