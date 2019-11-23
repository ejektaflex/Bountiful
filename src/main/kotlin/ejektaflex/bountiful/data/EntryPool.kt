package ejektaflex.bountiful.data

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.IEntryPool
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.generic.IIdentifiable
import ejektaflex.bountiful.compat.FacadeGameStages
import net.minecraft.world.World

// Currently unused? Should only need a PoolRegistry and a DecreeRegistry
open class EntryPool(override val id: String) : ValueRegistry<BountyEntry>(), IEntryPool {

    fun validEntries(world: World, worth: Int, alreadyPicked: List<String>): List<BountyEntry> {
        return validEntries(world).filter { it.minValueOfPick <= worth && it.content !in alreadyPicked }.sortedBy { it.minValueOfPick }
    }

    fun validEntries(world: World): List<BountyEntry> {
        // Was told client does not always know about all players
        return if (world.isRemote) {
            listOf()
        } else {
            return if (BountifulMod.config.isRunningGameStages) {
                content.filter { FacadeGameStages.anyPlayerHas(world, it.requiredStages()) }
            } else {
                content
            }
        }
    }
}