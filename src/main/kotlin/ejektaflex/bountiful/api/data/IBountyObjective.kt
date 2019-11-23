package ejektaflex.bountiful.api.data

import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.IBountyEntry
import net.minecraft.entity.player.PlayerEntity

interface IBountyObjective : IBountyEntry {
    /**
     * @return Whether or not the objective was considered to be complete after handling
     */
    fun handleObjective(player: PlayerEntity, data: IBountyData): Boolean
}