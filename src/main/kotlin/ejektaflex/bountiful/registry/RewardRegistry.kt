package ejektaflex.bountiful.registry

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
import ejektaflex.compat.FacadeGameStages
import net.minecraft.world.World

object RewardRegistry : ValueRegistry<PickedEntryStack>() {
    fun validRewards(world: World, worth: Int, alreadyPicked: List<String>): List<PickedEntryStack> {
        return validRewards(world).filter { it.genericPick.minValueOfPick <= worth && it.content !in alreadyPicked }.sortedBy { it.genericPick.minValueOfPick }
    }

    fun validRewards(world: World): List<PickedEntryStack> {
        // Was told client does not always know about all players
        return if (world.isRemote) {
            listOf()
        } else {
            return if (Bountiful.config.isRunningGameStages) {
                items.filter { FacadeGameStages.anyPlayerHas(world, it.genericPick.requiredStages()) }
            } else {
                items
            }
        }
    }
}