package com.example.recipe

import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType

class SolveTree(
    val value: ItemStack,
    val makes: Int,
    val type: RecipeType<*>?,
    val leaves: MutableList<SolveTree> = mutableListOf(),
    val parent: SolveTree.() -> SolveTree = {this}
) {
    fun isRoot() = parent() == this

    fun addLeaf(newValue: ItemStack, newMakes: Int, newType: RecipeType<*>): SolveTree {
        val newTree = SolveTree(newValue, newMakes, newType, mutableListOf()) { this }
        leaves.add(newTree)
        return newTree
    }
}