package ejektaflex.bountiful.data

import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.registry.PoolRegistry

data class Decree(
        override val decreeTitle: String = "UNKNOWN",
        override val decreeDescription: String = "UNKNOWN_DESC",
        override val id: String = "unknown_id",
        override val spawnsInBoard: Boolean = false,
        override val isGreedy: Boolean = false,
        override val objectivePools: MutableList<String> = mutableListOf(),
        override val rewardPools: MutableList<String> = mutableListOf()
) : IDecree {

    /**
    override val objectives: List<BountyEntry>
        get() = getEntriesFromTagList(objectivePools)

    override val rewards: List<BountyEntry>
        get() = getEntriesFromTagList(rewardPools)
    **/

    private fun getEntriesFromTagList(poolTags: MutableList<String>): List<BountyEntry<*>> {
        return PoolRegistry.content.filter { it.id in poolTags }.map { it.content }.flatten()
    }

}