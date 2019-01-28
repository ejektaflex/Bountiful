package ejektaflex.bountiful.registry

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.BountifulAPI
import ejektaflex.bountiful.api.logic.pickable.PickableEntry

object BountyRegistry : ValueRegistry<PickableEntry>() {
    fun validBounties(): List<PickableEntry> {
        if (Bountiful.config.isRunningGameStages) {
            return items.filter { true /* todo */ }
        } else {
            return items
        }
    }
}