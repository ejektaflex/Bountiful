package bountiful.registry

import bountiful.logic.PickableEntry

open class ValueRegistry {

    val items = mutableListOf<PickableEntry>()

    fun add(vararg items: PickableEntry) {
        for (item in items) {
            this.items.add(item)
        }
    }

}