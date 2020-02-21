package ejektaflex.bountiful.data

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.registry.PoolRegistry

data class Decree(
        @Expose override val decreeTitle: String = "UNKNOWN",
        @Expose override val decreeDescription: String = "UNKNOWN_DESC",
        @Expose override val id: String = "unknown_id",
        @Expose override val spawnsInBoard: Boolean = false,
        @Expose override val isGreedy: Boolean = false,
        @Expose override val objectivePools: MutableList<String> = mutableListOf(),
        @Expose override val rewardPools: MutableList<String> = mutableListOf()
) : IDecree {

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