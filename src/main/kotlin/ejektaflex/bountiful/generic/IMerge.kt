package ejektaflex.bountiful.generic

interface IMerge<T : Any> {

    val canLoad: Boolean

    fun merge(other: T)

}