package io.ejekta.bountiful.common.content.board

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.inventory.Inventories
import net.minecraft.util.collection.DefaultedList


class BoardInventory : Inventory {

    val content: DefaultedList<ItemStack> = DefaultedList.ofSize(BoardBlock.SIZE, ItemStack.EMPTY)

    override fun canPlayerUse(player: PlayerEntity) = true

    override fun clear() = content.clear()

    override fun isEmpty() = content.isEmpty()

    override fun getStack(slot: Int): ItemStack {
        return content[slot]
    }

    override fun size(): Int {
        return content.size
    }

    override fun markDirty() {
        println("BoardInv marked as dirty!")
    }

    override fun setStack(slot: Int, stack: ItemStack?) {
        stack?.let { content[slot] = it }
    }

    override fun removeStack(slot: Int, count: Int): ItemStack? {
        val result = Inventories.splitStack(content, slot, count)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    override fun removeStack(slot: Int): ItemStack? {
        return Inventories.removeStack(content, slot)
    }

}