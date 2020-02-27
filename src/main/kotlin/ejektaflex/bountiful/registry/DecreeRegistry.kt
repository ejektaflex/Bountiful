package ejektaflex.bountiful.registry

import ejektaflex.bountiful.api.data.IDecree
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.data.Decree
import ejektaflex.bountiful.data.ValueRegistry
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.alexwells.kottle.FMLKotlinModLoadingContext
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.ModLoader
import net.minecraftforge.fml.javafmlmod.FMLModContainer

object DecreeRegistry : ValueRegistry<Decree>() {

    fun getDecree(id: String): Decree? {
        return content.firstOrNull { it.id == id }
    }


    val allObjectives: List<BountyEntry>
        get() = getObjectives(content)

    private fun getEntryList(decrees: List<IDecree>, pools: IDecree.() -> List<String>): List<BountyEntry> {
        // Pool string list -> Pool list -> Only pools with all required mods aquired -> pool entries -> flattened
        return decrees.asSequence().map {
            pools(it)
        }.flatten().toSet().map {
            PoolRegistry.poolFor(it)!!
        }.filter {
            it.modsRequired?.all { ModList.get().isLoaded(it) } ?: true
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