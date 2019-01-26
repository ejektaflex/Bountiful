package ejektaflex.bountiful.api.ext

import ejektaflex.bountiful.api.logic.picked.IPickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntry
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

fun NBTTagCompound.clear() {
    for (key in keySet) {
        removeTag(key)
    }
}

fun NBTTagCompound.setUnsortedList(key: String, items: Set<INBTSerializable<NBTTagCompound>>) {
    val listTag = NBTTagCompound().apply {
        items.forEachIndexed { index, item ->
            setTag(index.toString(), item.serializeNBT())
        }
    }
    setTag(key, listTag)
}

fun <T : INBTSerializable<NBTTagCompound>> NBTTagCompound.getUnsortedList(key: String, itemGen: () -> T): Set<T> {
    val listTag = getCompoundTag(key)
    return listTag.keySet.map { index ->
        val itag = listTag.getCompoundTag(index)
        itemGen().apply { deserializeNBT(itag) }
    }.toSet()
}

fun NBTTagCompound.getPickedEntryList(key: String): Set<IPickedEntry> {
    val listTag = getCompoundTag(key)
    return listTag.keySet.map { index ->
        val itag = listTag.getCompoundTag(index)
        PickedEntry().apply { deserializeNBT(itag) }.typed().apply { deserializeNBT(itag) }
    }.toSet()
}