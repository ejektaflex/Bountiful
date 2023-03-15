package ejektaflex.bountiful.data.bounty

import net.minecraft.network.chat.Component

interface IBountyObjective {

    fun tooltipObjective(progress: BountyProgress): Component

    /*
    fun checkComplete(world: World, player: PlayerEntity, data: IBountyData): Boolean {
        return true
    }

    fun consume(world: World, player: PlayerEntity, data: IBountyData)


     */
}