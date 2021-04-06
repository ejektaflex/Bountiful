package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.bountiful.util.readOnlyCopy
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack

class BountyInventory : SimpleInventory(SIZE) {

    fun addBounty(slot: Int, data: BountyData? = null) {
        if (slot !in bountySlots) return
        setStack(slot, BountyItem.create(data))
    }

    override fun onOpen(player: PlayerEntity?) {
        println("Inv opened")
        super.onOpen(player)
    }

    override fun onClose(player: PlayerEntity?) {
        println("Inv closed")
        super.onClose(player)
    }

    val numBounties: Int
        get() = readOnlyCopy.count { it.item == BountifulContent.BOUNTY_ITEM }

    fun cloned(blacklist: List<ItemStack>): BountyInventory {
        val newInv = BountyInventory()
        for (i in 0 until size()) {
            val stack = getStack(i)
            // do not sync over any stacks the player had in their inventory
            if (!blacklist.any { it.item is BountyItem && ItemStack.areTagsEqual(it, stack) }) {
                newInv.setStack(i, stack.copy())
            }
        }
        return newInv
    }

    fun cloned(mask: Set<Int>): BountyInventory {
        val newInv = BountyInventory()
        val valid = (0 until size()).filter { it !in mask }
        for (i in valid) {
            val stack = getStack(i)
            newInv.setStack(i, stack.copy())
            // TODO do not sync over any stacks the player had in their inventory
            /*
            if (!blacklist.any { it.item is BountyItem && ItemStack.areTagsEqual(it, stack) }) {
                newInv.setStack(i, stack.copy())
            }

             */
        }
        return newInv
    }

    companion object {
        const val SIZE = 21
        val bountySlots = 0 until 21
    }
}