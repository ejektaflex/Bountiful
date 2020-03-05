package ejektaflex.bountiful.generic

import com.google.gson.annotations.Expose

open class ValueRegistry<T : Any> {

    @Expose
    val content = mutableListOf<T>()

    fun add(vararg items: T) {
        for (item in items) {
            this.content.add(item)
        }
    }

    fun remove(item: T) = content.remove(item)

    fun backup(): List<T> {
        return mutableListOf<T>().apply { addAll(content) }
    }

    fun restore(backupList: List<T>) {
        empty()
        content.addAll(backupList)
    }

    fun empty() {
        content.clear()
    }

    operator fun iterator() = content.iterator()

    fun replace(newItems: List<T>, condition: T.() -> Boolean): List<T> {
        val invalids = mutableListOf<T>()
        val dataBackup = backup()
        this.empty()
        for (item in newItems) {
            if (condition(item)) {
                this.add(item)
            } else {
                invalids.add(item)
            }
        }
        // If there was any invalid data, toss it and restore the old, valid data
        if (invalids.isNotEmpty()) {
            restore(dataBackup)
        }
        return invalids
    }

}


