package ejektaflex.bountiful.util

interface IMerge<T : Any> {

    val canLoad: Boolean

    fun merge(other: T)

}