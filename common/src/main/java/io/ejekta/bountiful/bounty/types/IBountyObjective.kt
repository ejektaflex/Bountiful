package io.ejekta.bountiful.bounty.types

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity

interface IBountyObjective : IBountyType {
    fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress {
        return Progress(entry.current, entry.amount)
    }

    fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        return entry.current >= entry.amount
    }

    fun getNewCurrent(entry: BountyDataEntry, player: PlayerEntity): Int {
        return entry.current
    }

}