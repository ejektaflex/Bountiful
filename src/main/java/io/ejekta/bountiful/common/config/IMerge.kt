package io.ejekta.bountiful.common.config

interface IMerge<T : IMerge<T>> {
    fun merge(other: T)
    fun merged(other: T): T
}