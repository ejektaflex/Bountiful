package io.ejekta.bountiful.bounty.types

import io.ejekta.bountiful.bounty.BountyDataEntry
import net.minecraft.entity.player.PlayerEntity

interface IBountyObjective : IBountyType {
    fun getProgress(entry: BountyDataEntry, player: PlayerEntity): Progress

    fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity): Boolean


}