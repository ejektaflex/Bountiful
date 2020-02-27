package ejektaflex.bountiful.logic

import ejektaflex.bountiful.api.data.IBountyData
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World

interface IBountyObjective {

    fun tooltipObjective(progress: BountyProgress): ITextComponent

    /*
    fun checkComplete(world: World, player: PlayerEntity, data: IBountyData): Boolean {
        return true
    }

    fun consume(world: World, player: PlayerEntity, data: IBountyData)


     */
}