package ejektaflex.bountiful.api.ext

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

fun NBTTagCompound.clear() {
    for (key in keySet) {
        removeTag(key)
    }
}

fun NBTTagCompound.setSet(key: String, items: Set<INBTSerializable<NBTTagCompound>>) {
    val listTag = NBTTagCompound().apply {
        items.forEachIndexed { index, item ->
            setTag(index.toString(), item.serializeNBT())
        }
    }
    setTag(key, listTag)
}

fun <T : INBTSerializable<NBTTagCompound>> NBTTagCompound.getSet(key: String, itemGen: () -> T): Set<T> {
    return (getTag(key) as NBTTagCompound).keySet.map { index ->
        itemGen().apply { deserializeNBT(getTag(index) as NBTTagCompound) }
    }.toSet()
}
