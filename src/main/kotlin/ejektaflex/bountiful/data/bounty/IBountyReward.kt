package ejektaflex.bountiful.data.bounty

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.ITextComponent

interface IBountyReward {

    fun tooltipReward(): ITextComponent


    fun reward(player: PlayerEntity)

}