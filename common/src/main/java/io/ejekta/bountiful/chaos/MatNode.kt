package io.ejekta.bountiful.chaos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack

@Serializable
data class MatNode(
    val stack: @Contextual ItemStack,
    val factor: Double = 1.0,
    var parent: MatSet? = null,
    val sets: MutableList<MatSet> = mutableListOf()
) {

    fun addSet(newSet: MatSet) {
        newSet.parent?.removeSet(newSet)
        newSet.parent = this
        sets.add(newSet)
    }

    private fun removeSet(currSet: MatSet) {
        sets.remove(currSet)
        currSet.parent = null
    }

}