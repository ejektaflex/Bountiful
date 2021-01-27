package ejektaflex.bountiful.data.bounty

import net.minecraft.util.text.ITextComponent

interface IBountyObjective {

    fun tooltipObjective(progress: BountyProgress): ITextComponent

    /*
    fun checkComplete(world: World, player: PlayerEntity, data: IBountyData): Boolean {
        return true
    }

    fun consume(world: World, player: PlayerEntity, data: IBountyData)


     */
}