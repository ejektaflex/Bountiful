package ejektaflex.bountiful.logic

import ejektaflex.bountiful.data.entry.BountyType
import ejektaflex.bountiful.data.ValueRegistry

object BountyTypeRegistry : ValueRegistry<String>() {

    init {
        add(*BountyType.values().map { it.id }.toTypedArray())
    }

}