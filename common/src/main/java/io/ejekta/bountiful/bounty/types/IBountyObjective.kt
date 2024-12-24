package io.ejekta.bountiful.bounty.types

import io.ejekta.bountiful.components.BountyDataEntry
import net.minecraft.world.entity.player.Player

interface IBountyObjective : IBountyType {
    fun getProgress(entry: BountyDataEntry, player: Player, current: Int): Progress {
        return Progress(current, entry.amount)
    }

    fun consumeObjectives(entry: BountyDataEntry, player: Player, current: Int): Boolean {
        return current >= entry.amount
    }

    fun getNewCurrent(entry: BountyDataEntry, player: Player, current: Int): Int {
        return current
    }
}