package ejektaflex.bountiful.ext

import ejektaflex.bountiful.BountifulMod
import net.minecraft.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.ModList

val ItemStack.toNBT: CompoundTag
    get() {
        return CompoundTag().apply {
            putString("e_item", toPretty)
            putInt("e_amt", count)
            put("e_nbt", serializeNBT())
        }
    }

val CompoundTag.toItemStack: ItemStack?
    get() {
        val istack = getString("e_item").toItemStack
        return istack?.apply {
            count = getInt("e_amt")
            tag = get("e_nbt") as CompoundTag
        }
    }

inline fun <reified T : Item> ItemStack.edit(func: T.(stack: ItemStack) -> Unit) {
    if (item is T) {
        func(item as T, this)
    } else {
        throw Exception("Tried to edit stack ${stack.count}x ${stack.item.registryName} as if it was a ${T::class}")
    }
}

/*
inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.editNbt(func: T.() -> Unit) {
    if ()
}

 */

inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.toData(func: () -> T): T {
    return func().apply {
        deserializeNBT(stack.tag)
    }
}

inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.toSafeData(func: () -> T): T? {
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
