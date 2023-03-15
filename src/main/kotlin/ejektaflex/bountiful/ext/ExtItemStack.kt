package ejektaflex.bountiful.ext

import ejektaflex.bountiful.BountifulMod
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.ModList
import net.minecraftforge.registries.ForgeRegistries

val ItemStack.toNBT: CompoundTag
    get() {
        return CompoundTag().apply {
            putString("e_item", toPretty)
            putInt("e_amt", count)
            put("e_nbt", serializeNBT())
        }
    }


val Entity.registryName: ResourceLocation?
    get() = ForgeRegistries.ENTITY_TYPES.getKey(type)

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
        throw Exception("Tried to edit stack ${count}x ${item.registryName} as if it was a ${T::class}")
    }
}

/*
inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.editNbt(func: T.() -> Unit) {
    if ()
}

 */

inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.toData(func: () -> T): T {
    return func().apply {
        deserializeNBT(tag)
    }
}

inline fun <reified T : INBTSerializable<CompoundTag>> ItemStack.toSafeData(func: () -> T): T? {
    return try {
        func().apply {
            deserializeNBT(tag)
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
