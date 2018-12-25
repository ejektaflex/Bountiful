package ejektaflex.bountiful.ext

import net.minecraft.nbt.NBTTagCompound

fun NBTTagCompound.clear() {
    for (key in keySet) {
        removeTag(key)
    }
}