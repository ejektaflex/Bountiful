package io.ejekta.bountiful.data

import io.ejekta.bountiful.bridge.Bountybridge

interface IMerge<T : IMerge<T>> {
    var id: String

    val replace: Boolean

    val requires: MutableList<String>

    val canLoad: Boolean
        get() = requires.all { Bountybridge.isModLoaded(it) }

    fun merged(other: T): T

    fun finishMergedSetup() {

    }

}