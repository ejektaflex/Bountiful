package ejektaflex.bountiful.api.registry


interface IValueRegistry<T : Any> {
    fun add(vararg items: T)
    fun remove(item: T): Boolean
    fun backup(): List<T>
    fun restore(backupList: List<T>)
    fun empty()
    fun <U : T> typedItems(): List<T>
}