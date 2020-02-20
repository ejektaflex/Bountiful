package ejektaflex.bountiful.registry

import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.data.ValueRegistry

object DecreeRegistry : ValueRegistry<IDecree>() {

    fun getDecree(id: String): IDecree? {
        return content.firstOrNull { it.id == id }
    }

    val allObjectives: List<BountyEntry<*>>
        get() = getObjectives(content)

    private fun getEntryList(decrees: List<IDecree>, pools: IDecree.() -> List<String>): List<BountyEntry<*>> {
        return decrees.asSequence().map {
            pools(it)
        }.flatten().toSet().map {
            PoolRegistry.poolFor(it)!!.content
        }.flatten()
    }

    // Get all objective pool string from decrees, then get said pools
    fun getObjectives(decrees: List<IDecree>): List<BountyEntry<*>> {
        return getEntryList(decrees) {
            objectivePools
        }
    }

    fun getRewards(decrees: List<IDecree>): List<BountyEntry<*>> {
        return getEntryList(decrees) {
            rewardPools
        }
    }


}