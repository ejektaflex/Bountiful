package ejektaflex.bountiful.data

interface ICache<T : Any, U : Any> {
    fun invalidate(item: T)
    fun store(item: T, data: U)
    operator fun contains(item: T): Boolean
}