package io.ejekta.bountiful.common.content

import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

class UniqueInv : BoardInventory {

    override val content: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)

    override fun markDirty() {
        println("UniqueInv marked as dirty!")
    }

}