package ejektaflex.bountiful.ext

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

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
