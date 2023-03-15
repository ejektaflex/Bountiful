package ejektaflex.bountiful.data.bounty

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

interface IBountyReward {

    fun tooltipReward(): Component


    fun reward(player: Player)

}