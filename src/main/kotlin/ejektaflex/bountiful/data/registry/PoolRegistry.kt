package ejektaflex.bountiful.data.registry

import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.util.ValueRegistry

object PoolRegistry : ValueRegistry<EntryPool>() {

    fun poolFor(id: String): EntryPool? {
        return content.firstOrNull { it.id == id }
    }

}