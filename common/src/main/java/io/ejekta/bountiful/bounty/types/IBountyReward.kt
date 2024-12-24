package io.ejekta.bountiful.bounty.types

import io.ejekta.bountiful.components.BountyDataEntry
import net.minecraft.world.entity.player.Player

interface IBountyReward : IBountyType {
    fun giveReward(entry: BountyDataEntry, player: Player)
}