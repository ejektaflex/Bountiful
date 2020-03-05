package ejektaflex.bountiful.data.structure

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.util.IMerge
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.util.IIdentifiable
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

data class Decree(
        @Expose var decreeTitle: String = "UNKNOWN",
        @Expose override var id: String = "unknown_id",
        @Expose var spawnsInBoard: Boolean = false,
        @Expose var isGreedy: Boolean = false,
        @Expose var objectivePools: MutableList<String> = mutableListOf(),
        @Expose var rewardPools: MutableList<String> = mutableListOf()
) : INBTSerializable<CompoundNBT>, IMerge<Decree>, IIdentifiable {

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