package ejektaflex.bountiful.data.registry

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.util.ValueRegistry
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraftforge.fml.ModList

object DecreeRegistry : ValueRegistry<Decree>() {

    val ids: List<String>
        get() = content.map { it.id }

    fun getDecree(id: String): Decree? {
        return content.firstOrNull { it.id == id }
    }

    private fun getEntryList(decrees: List<Decree>, pools: Decree.() -> List<String>): List<BountyEntry> {
        // Pool string list -> Pool list -> Only pools with all required mods acquired -> pool entries -> flattened
        return decrees.asSequence().map {
            pools(it)
        }.flatten().toSet().mapNotNull {
            val opt = PoolRegistry.poolFor(it)
            if (opt == null) {
                //BountifulMod.logger.error("Tried to get entry list for decrees ${decrees.map { d -> d.id }}; pool that was trying to be accessed: $it")
                // That decree doesn't exist anymore!
            }
            opt
        }.filter {
            it.modsRequired?.all { mName -> ModList.get().isLoaded(mName) } ?: true
        }.map {
            it.content
        }.flatten()
    }

    // Get all objective pool string from decrees, then get said pools
    fun getObjectives(decrees: List<Decree>): List<BountyEntry> {
        return getEntryList(decrees) {
            objectivePools
        }.filter {
            it is IBountyObjective
        }
    }

    fun getRewards(decrees: List<Decree>): List<BountyEntry> {
        return getEntryList(decrees) {
            rewardPools
        }.filter {
            it is IBountyReward
        }
    }


}