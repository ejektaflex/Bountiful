package ejektaflex.bountiful.registry

import ejektaflex.bountiful.logic.pickable.PickableEntry

open class ValueRegistry {

    val items = mutableListOf<PickableEntry>()

    fun add(vararg items: PickableEntry) {
        for (item in items) {
            this.items.add(item)
        }
    }

    fun backup(): List<PickableEntry> {
        return mutableListOf<PickableEntry>().apply { addAll(items) }
    }

    fun restore(backupList: List<PickableEntry>) {
        empty()
        items.addAll(backupList)
    }

    fun empty() {
        items.clear()
    }

}