package ejektaflex.bountiful.registry

import ejektaflex.bountiful.api.data.IEntryPool
import ejektaflex.bountiful.data.ValueRegistry

object PoolRegistry : ValueRegistry<IEntryPool>() {

    fun poolFor(id: String): IEntryPool? {
        return content.firstOrNull { it.id == id }
    }

}