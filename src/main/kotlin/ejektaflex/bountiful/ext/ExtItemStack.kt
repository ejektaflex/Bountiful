package ejektaflex.bountiful.ext

import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.fml.ModList

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

val ItemStack.modOriginName: String?
    get() {
        val modid = item.registryName?.namespace
        return if (modid != null) {
            ModList.get().mods.find { it.modId == modid }?.displayName
        } else {
            null
        }
    }
