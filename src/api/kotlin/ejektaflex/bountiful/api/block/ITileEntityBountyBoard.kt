package ejektaflex.bountiful.api.block

import ejektaflex.bountiful.api.logic.IBountyHolder
import net.minecraft.util.math.BlockPos
import net.minecraft.util.ITickable

interface ITileEntityBountyBoard : ITickable {
    /**
     * The inventory holder for this bounty board. Contains an ItemStackHandler
     */
    val inventory: IBountyHolder
    /**
     * Whether or not this board has just been placed
     */
    var newBoard: Boolean

    /**
     * Sends a redstone pulse through the bounty board block
     */
    fun sendRedstonePulse()
    
    /**
     * The BlockPos of the bounty board block
     */
    fun getBoardBlockPos(): BlockPos
}