package ejektaflex.bountiful.api

interface IMerge<T : Any> {

    val canLoad: Boolean

    fun merge(other: T)

}