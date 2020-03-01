package ejektaflex.bountiful.api

interface IMerge<T : Any> {

    fun merge(other: T)

}