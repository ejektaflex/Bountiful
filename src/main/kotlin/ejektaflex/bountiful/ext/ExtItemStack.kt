package ejektaflex.bountiful.ext

import ejektaflex.bountiful.BountifulMod
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.ModList
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor

val ItemStack.toNBT: CompoundNBT
    get() {
        return CompoundNBT().apply {
            putString("e_item", toPretty)
            putInt("e_amt", count)
            put("e_nbt", serializeNBT())
        }
    }

val CompoundNBT.toItemStack: ItemStack?
    get() {
        val istack = getString("e_item").toItemStack
        return istack?.apply {
            count = getInt("e_amt")
            tag = get("e_nbt") as CompoundNBT
        }
    }

inline fun <reified T : Item> ItemStack.edit(func: T.(it: ItemStack) -> Unit) {
    if (item is T) {
        func(item as T, this)
    } else {
        throw Exception("Tried to edit stack ${stack.count}x ${stack.item.registryName} as if it was a ${T::class}")
    }
}

/*
inline fun <reified T : INBTSerializable<CompoundNBT>> ItemStack.editNbt(func: T.() -> Unit) {
    if ()
}

 */

inline fun <reified T : INBTSerializable<CompoundNBT>> ItemStack.toData(func: () -> T): T {
    return func().apply {
        deserializeNBT(stack.tag)
    }
}

inline fun <reified T : INBTSerializable<CompoundNBT>> ItemStack.toSafeData(func: () -> T): T? {
    return try {
        func().apply {
            deserializeNBT(stack.tag)
        }
    } catch (e: Exception) {
        BountifulMod.logger.error(e.message)
        null
    }
}

val ItemStack.modOriginName: String?
    get() {
        val modid = item.registryName?.namespace
        return if (modid != null) {
            ModList.get().mods.find { it.modId == modid }?.displayName
        } else {
            null
        }
    }
