package ejektaflex.bountiful.data

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.IMerge
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

data class Decree(
        @Expose override var decreeTitle: String = "UNKNOWN",
        @Expose override var id: String = "unknown_id",
        @Expose override var spawnsInBoard: Boolean = false,
        @Expose override var isGreedy: Boolean = false,
        @Expose override var objectivePools: MutableList<String> = mutableListOf(),
        @Expose override var rewardPools: MutableList<String> = mutableListOf()
) : IDecree, INBTSerializable<CompoundNBT>, IMerge<Decree> {

    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putString("id", this@Decree.id)
            putString("title", this@Decree.decreeTitle)
        }
    }

    override val canLoad: Boolean
        get() = true

    override fun deserializeNBT(nbt: CompoundNBT?) {
        id = nbt!!.getString("id")
        decreeTitle = nbt.getString("title")
    }

    override fun merge(other: Decree) {
        objectivePools = mutableSetOf(*(objectivePools + other.objectivePools).toTypedArray()).toMutableList()
        rewardPools = mutableSetOf(*(rewardPools + other.rewardPools).toTypedArray()).toMutableList()
    }

    /**
    override val objectives: List<BountyEntry>
        get() = getEntriesFromTagList(objectivePools)

    override val rewards: List<BountyEntry>
        get() = getEntriesFromTagList(rewardPools)
    **/

    private fun getEntriesFromTagList(poolTags: MutableList<String>): List<BountyEntry> {
        return PoolRegistry.content.filter { it.id in poolTags }.map { it.content }.flatten()
    }

}