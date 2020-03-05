package ejektaflex.bountiful.logic

import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.generic.ValueRegistry

object BountyTypeRegistry : ValueRegistry<String>() {

    init {
        add(*BountyType.values().map { it.id }.toTypedArray())
    }

}