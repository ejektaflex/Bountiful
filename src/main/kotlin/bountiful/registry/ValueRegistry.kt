package bountiful.registry

import bountiful.logic.pickable.PickableEntry

open class ValueRegistry {

    val items = mutableListOf<PickableEntry>()

    fun add(vararg items: PickableEntry) {
        for (item in items) {
            this.items.add(item)
        }
    }

    fun empty() {
        items.clear()
    }

}