package ejektaflex.bountiful.registry

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.IDecree
import ejektaflex.bountiful.data.entry.BountyEntry
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.ValueRegistry
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraftforge.fml.ModList

object DecreeRegistry : ValueRegistry<Decree>() {

    fun getDecree(id: String): Decree? {
        return content.firstOrNull { it.id == id }
    }

    private fun getEntryList(decrees: List<IDecree>, pools: IDecree.() -> List<String>): List<BountyEntry> {
        // Pool string list -> Pool list -> Only pools with all required mods acquired -> pool entries -> flattened
        return decrees.asSequence().map {
            pools(it)
        }.flatten().toSet().mapNotNull {
            val opt = PoolRegistry.poolFor(it)
            if (opt == null) {
                BountifulMod.logger.error("Tried to get entry list for decrees ${decrees.map { d -> d.id }}; pool that was trying to be accessed: $it")
            }
            opt
        }.filter {
            it.modsRequired?.all { mName -> ModList.get().isLoaded(mName) } ?: true
        }.map {
            it.content
        }.flatten()
    }

    // Get all objective pool string from decrees, then get said pools
    fun getObjectives(decrees: List<IDecree>): List<BountyEntry> {
        return getEntryList(decrees) {
            objectivePools
        }.filter {
            it is IBountyObjective
        }
    }

    fun getRewards(decrees: List<IDecree>): List<BountyEntry> {
        return getEntryList(decrees) {
            rewardPools
        }.filter {
            it is IBountyReward
        }
    }


}