package ejektaflex.bountiful.data

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

data class Decree(
        @Expose override val decreeTitle: String = "UNKNOWN",
        @Expose override val decreeDescription: String = "UNKNOWN_DESC",
        @Expose override var id: String = "unknown_id",
        @Expose override val spawnsInBoard: Boolean = false,
        @Expose override val isGreedy: Boolean = false,
        @Expose override val objectivePools: MutableList<String> = mutableListOf(),
        @Expose override val rewardPools: MutableList<String> = mutableListOf()
) : IDecree, INBTSerializable<CompoundNBT> {

    override fun serializeNBT(): CompoundNBT {
        return CompoundNBT().apply {
            putString("id", this@Decree.id)
        }
    }

    override fun deserializeNBT(nbt: CompoundNBT?) {
        id = nbt!!.getString("id")
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