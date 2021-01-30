package io.ejekta.bountiful.common.content.gui

import net.minecraft.inventory.Inventory
import net.minecraft.screen.slot.Slot

class BoardDecreeSlot(inv: Inventory, index: Int, x: Int, y: Int) : Slot(inv, index, x, y) {

    //override fun canInsert(stack: ItemStack?) = stack?.item == BountifulContent.BOUNTY_ITEM

}