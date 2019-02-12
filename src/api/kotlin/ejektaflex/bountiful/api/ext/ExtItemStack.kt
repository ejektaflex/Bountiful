package ejektaflex.bountiful.api.ext

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.Loader

val ItemStack.toNBT: NBTTagCompound
    get() {
        return NBTTagCompound().apply {
            setString("e_item", toPretty)
            setInteger("e_amt", count)
            setTag("e_nbt", serializeNBT())
        }
    }

val NBTTagCompound.toItemStack: ItemStack?
    get() {
        val istack = getString("e_item").toItemStack
        return istack?.apply {
            count = getInteger("e_amt")
            tagCompound = getCompoundTag("e_nbt")
        }
    }

val ItemStack.modOriginName: String?
    get() {
        val modid = item.registryName?.namespace
        return if (modid != null) {
            Loader.instance().modList.find { it.modId == modid }?.name
        } else {
            null
        }
    }
