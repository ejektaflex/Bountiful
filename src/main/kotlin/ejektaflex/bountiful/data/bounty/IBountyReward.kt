package ejektaflex.bountiful.data.bounty

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.chat.Component

interface IBountyReward {

    fun tooltipReward(): Component


    fun reward(player: PlayerEntity)

}