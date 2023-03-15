package ejektaflex.bountiful.ext

import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.enums.BountyType
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.util.INBTSerializable
import kotlin.reflect.full.createInstance

fun CompoundTag.clear() {
    for (key in allKeys) {
        remove(key)
    }
}

fun CompoundTag.setUnsortedList(key: String, items: Set<INBTSerializable<CompoundTag>>) {
    val listTag = CompoundTag().apply {
        items.forEachIndexed { index, item ->
            put(index.toString(), item.serializeNBT())
        }
    }
    put(key, listTag)
}

fun CompoundTag.setUnsortedListOfNbt(key: String, items: Set<CompoundTag>) {
    val listTag = CompoundTag().apply {
        items.forEachIndexed { index, nbt ->
            put(index.toString(), nbt)
        }
    }
    put(key, listTag)
}

fun CompoundTag.getUnsortedList(key: String): Set<CompoundTag> {
    val listTag = get(key) as? CompoundTag ?: return setOf()
    return listTag.allKeys.map { index ->
        listTag.get(index) as CompoundTag
    }.toSet()
}

fun <T : INBTSerializable<CompoundTag>> CompoundTag.getUnsortedListTyped(key: String, fact: () -> T): Set<T> {
    val listTag = get(key) as CompoundTag
    return listTag.allKeys.map { index ->
        listTag.get(index) as CompoundTag
    }.map {
        fact().apply {
            deserializeNBT(it)
        }
    }.toSet()
}


val CompoundTag.toBountyEntry: BountyEntry
    get() {
        val bTypeName = getString("type")
        val bType = BountyType.values().find { bTypeName == it.id }
                ?: throw Exception("Deserialized bounty with type: $bTypeName")
        val newBounty = bType.klazz.createInstance()
        return newBounty.apply { deserializeNBT(this@toBountyEntry) }
    }

