package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyProgress
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.entity.player.Player

abstract class CheckHandler<T : BountyEntry>() {

    lateinit var player: Player
    lateinit var data: BountyData
    lateinit var inv: NonNullList<ItemStack>

    constructor(inPlayer: Player, inData: BountyData) : this()

    fun initialize(inPlayer: Player, inData: BountyData) {
        player = inPlayer
        data = inData
        inv = player.inventory.items
    }

    abstract fun fulfill()

    abstract fun objectiveStatus(): Map<BountyEntry, BountyProgress>

    val isComplete: Boolean
        get() = objectiveStatus().isEmpty() || objectiveStatus().all { it.value.isFinished }

}