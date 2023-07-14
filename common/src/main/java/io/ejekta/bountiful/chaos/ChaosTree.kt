package io.ejekta.bountiful.chaos

import net.minecraft.item.ItemStack

class ChaosTree {
    val leaves = mutableMapOf<ItemStack, ChaosTree>()

    val size: Int
        get() = leaves.size + leaves.values.sumOf { it.size }
}