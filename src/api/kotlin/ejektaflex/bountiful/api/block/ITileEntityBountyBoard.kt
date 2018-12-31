package ejektaflex.bountiful.api.block

import ejektaflex.bountiful.api.logic.IBountyHolder
import net.minecraft.util.ITickable
import net.minecraftforge.items.ItemStackHandler

interface ITileEntityBountyBoard : ITickable {
    val inventory: IBountyHolder
    var newBoard: Boolean
    fun sendRedstonePulse()
}