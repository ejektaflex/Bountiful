package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountyItem
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.inventory.SimpleInventory
import net.minecraft.util.collection.DefaultedList


class BoardInventory(
    bountySrc: Inventory = BountyInventory(),
    decreeSrc: Inventory = SimpleInventory(3)
) : DoubleInventory(
    bountySrc,
    decreeSrc
) {

    //val content: DefaultedList<ItemStack> = DefaultedList.ofSize(BoardBlock.BOUNTY_SIZE, ItemStack.EMPTY)





    override fun canPlayerUse(player: PlayerEntity) = true

    companion object {
        val decreeSlots = 21 until 24
    }

}