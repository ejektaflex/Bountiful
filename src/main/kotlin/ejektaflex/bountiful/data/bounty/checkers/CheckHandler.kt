package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyProgress
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.util.NonNullList

abstract class CheckHandler<T : BountyEntry>() {

    lateinit var player: PlayerEntity
    lateinit var data: BountyData
    lateinit var inv: NonNullList<ItemStack>

    constructor(inPlayer: PlayerEntity, inData: BountyData) : this()

    fun initialize(inPlayer: PlayerEntity, inData: BountyData) {
        player = inPlayer
        data = inData
        inv = player.inventory.mainInventory
    }

    abstract fun fulfill()

    abstract fun objectiveStatus(): Map<BountyEntry, BountyProgress>

    val isComplete: Boolean
        get() = objectiveStatus().isEmpty() || objectiveStatus().all { it.value.isFinished }

}