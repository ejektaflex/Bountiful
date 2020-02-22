package ejektaflex.bountiful.logic

import ejektaflex.bountiful.data.ValueRegistry

object BountyTypeRegistry : ValueRegistry<String>() {

    init {
        add("stack", "tag", "entity")
    }

}