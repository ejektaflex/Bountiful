package io.ejekta.bountiful.data

import net.fabricmc.loader.api.FabricLoader

interface IMerge<T : IMerge<T>> {
    var id: String

    val replace: Boolean

    val requires: MutableList<String>

    val canLoad: Boolean
        get() = requires.all { FabricLoader.getInstance().isModLoaded(it) }

    fun merged(other: T): T

}