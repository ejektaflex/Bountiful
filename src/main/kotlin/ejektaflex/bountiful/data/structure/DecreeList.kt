package ejektaflex.bountiful.data.structure

import ejektaflex.bountiful.ext.getUnsortedList
import ejektaflex.bountiful.ext.setUnsortedListOfNbt
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.common.util.INBTSerializable

class DecreeList : INBTSerializable<CompoundTag> {

    var ids: MutableList<String> = mutableListOf()

    override fun deserializeNBT(nbt: CompoundTag) {
        ids = nbt.getUnsortedList("ids").map { tag ->
            tag.getString("id")
        }.toMutableList()
    }

    operator fun plus(other: DecreeList): DecreeList {
        return DecreeList().apply {
            this.ids = (this@DecreeList.ids + other.ids).toSet().toMutableList()
        }
    }

    override fun serializeNBT(): CompoundTag {
        return CompoundTag().apply {
            setUnsortedListOfNbt("ids", ids.map { id ->
                CompoundTag().apply {
                    putString("id", id)
                }
            }.toSet())
        }
    }

}