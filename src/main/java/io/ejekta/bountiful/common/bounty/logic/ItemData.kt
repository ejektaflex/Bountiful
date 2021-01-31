package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.serial.Format
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag

abstract class ItemData<T>(val ser: KSerializer<T>) {

    abstract val creator: () -> T

    operator fun get(stack: ItemStack) : T {
        return if (stack.hasTag()) {
            val data = stack.tag!!.toJson()
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
        val data = stack.tag!!.toJson()
        return Format.NBT.decodeFromJsonElement(ser, data)
    }

    operator fun set(stack: ItemStack, value: T) {
        stack.tag = Format.NBT.encodeToJsonElement(ser, value).toTag() as CompoundTag
    }

    fun edit(stack: ItemStack, func: T.() -> Unit) {
        get(stack).apply(func).also { set(stack, it) }
    }

    fun setSafeData(stack: ItemStack): T {
        return creator().apply {
            stack.tag = Format.NBT.encodeToJsonElement(ser, this).toTag() as CompoundTag
        }
    }

}