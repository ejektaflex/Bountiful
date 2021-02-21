package io.ejekta.bountiful.common.data

import net.fabricmc.loader.api.FabricLoader

interface IMerge<T : IMerge<T>> {
    var id: String

    val requires: MutableList<String>

    val canLoad: Boolean
        get() = requires.all { FabricLoader.getInstance().isModLoaded(it) }

    fun merge(other: T)

    fun merged(other: T): T

}