package ejektaflex.bountiful.api.data

interface IValueRegistry<T : Any> {
    val content: MutableList<T>
    fun add(vararg items: T)
    fun remove(item: T): Boolean
    fun backup(): List<T>
    fun restore(backupList: List<T>)
    fun empty()
    /**
     * Attempts to replace the contents of the registry with the given list, where all contents
     * must pass a given condition. If any fail, it instead reverts to it's original contents
     * and returns the failed items.
     */
    fun replace(newItems: List<T>, condition: T.() -> Boolean): List<T>
}