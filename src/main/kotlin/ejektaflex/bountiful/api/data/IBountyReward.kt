package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.data.entry.IBountyEntry
import net.minecraft.entity.player.PlayerEntity

interface IBountyReward : IBountyEntry {
    /**
     * @return The amount of worth that the reward ended up being worth
     */
    fun handleReward(player: PlayerEntity, data: IBountyData): Int
}