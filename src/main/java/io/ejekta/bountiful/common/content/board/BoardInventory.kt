package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountyItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.inventory.Inventories
import net.minecraft.util.collection.DefaultedList


class BoardInventory : Inventory {

    val content: DefaultedList<ItemStack> = DefaultedList.ofSize(BoardBlock.SIZE, ItemStack.EMPTY)

    val bountySlots = 0 until 21
    val decreeSlots = 21 until 24

    fun addBounty(slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        setStack(slot, BountyItem.create(data))
    }

    fun addRandomBounty(data: BountyData? = null): Int {
        val slot = bountySlots.random()
        setStack(slot, BountyItem.create(data))
        return slot
    }

    fun removeRandomBounty(except: Int) {
        setStack((bountySlots - except).random(), ItemStack.EMPTY)
    }

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