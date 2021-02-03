package io.ejekta.bountiful.common.bounty.data.pool

interface IMerge<T : IMerge<T>> {
    fun merge(other: T)
    fun merged(other: T): T
}