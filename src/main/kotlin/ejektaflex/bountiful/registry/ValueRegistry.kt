package ejektaflex.bountiful.registry

import ejektaflex.bountiful.api.registry.IValueRegistry

open class ValueRegistry<T : Any> : IValueRegistry<T> {

    val items = mutableListOf<T>()

    override fun add(vararg items: T) {
        for (item in items) {
            this.items.add(item)
        }
    }

    override fun remove(item: T) = items.remove(item)

    override fun backup(): List<T> {
        return mutableListOf<T>().apply { addAll(items) }
    }

    override fun restore(backupList: List<T>) {
        empty()
        items.addAll(backupList)
    }

    override fun empty() {
        items.clear()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <U : T> typedItems(): List<U> {
        return items.mapNotNull { it as? U }
    }

}