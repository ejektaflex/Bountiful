package ejektaflex.bountiful.registry

import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.generic.ValueRegistry

object PoolRegistry : ValueRegistry<EntryPool>() {

    fun poolFor(id: String): EntryPool? {
        return content.firstOrNull { it.id == id }
    }

}