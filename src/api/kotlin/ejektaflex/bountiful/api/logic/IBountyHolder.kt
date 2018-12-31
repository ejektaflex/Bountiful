package ejektaflex.bountiful.api.logic

import ejektaflex.bountiful.api.block.ITileEntityBountyBoard
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler

interface IBountyHolder {
    val handler: ItemStackHandler
    fun tickBounties(world: World)
    // Updates the bounty holder. Returns true if data has been marked dirty.
    fun update(world: World, te: ITileEntityBountyBoard?): Boolean
    fun addSingleBounty(world: World, te: ITileEntityBountyBoard?)
}