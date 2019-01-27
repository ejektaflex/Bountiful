package ejektaflex.adapter

interface IAdapter<T : Any> {
    fun adapt(): T
}