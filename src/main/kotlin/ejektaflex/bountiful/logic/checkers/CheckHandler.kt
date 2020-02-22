package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.logic.BountyProgress
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

abstract class CheckHandler<T : BountyEntry>() {

    lateinit var player: PlayerEntity
    lateinit var data: IBountyData
    lateinit var inv: NonNullList<ItemStack>

    constructor(inPlayer: PlayerEntity, inData: IBountyData, inInv: NonNullList<ItemStack>) : this() {}

    fun initialize(inPlayer: PlayerEntity, inData: IBountyData) {
        player = inPlayer
        data = inData
        inv = player.inventory.mainInventory
    }

    abstract fun fulfill()

    abstract fun objectiveStatus(): Map<BountyEntry, BountyProgress>

    val isComplete: Boolean
        get() = objectiveStatus().isEmpty() || objectiveStatus().all { it.value == BountyProgress.DONE }

}