package ejektaflex.bountiful.util

interface IMerge<T : Any> : IIdentifiable {

    val canLoad: Boolean

    fun merge(other: T)

}