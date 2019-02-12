package ejektaflex.bountiful.registry

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.compat.FacadeGameStages
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

object BountyRegistry : ValueRegistry<PickableEntry>() {
    fun validBounties(world: World): List<PickableEntry> {
        return if (Bountiful.config.isRunningGameStages) {
            items.filter { FacadeGameStages.anyPlayerHas(world, it.requiredStages()) }
        } else {
            items
        }
    }
}