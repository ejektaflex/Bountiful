package io.ejekta.bountiful.content.board

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory


class BoardInventory(
    bountySrc: Inventory = BountyInventory(),
    decreeSrc: Inventory = SimpleInventory(3)
) : DoubleInventory(
    bountySrc,
    decreeSrc
) {

    override fun canPlayerUse(player: PlayerEntity) = true

    companion object {
        val decreeSlots = 21 until 24
    }

}