package ejektaflex.bountiful.api.block

import net.minecraft.util.ITickable
import net.minecraftforge.items.ItemStackHandler

interface ITileEntityBountyBoard : ITickable {
    val inventory: ItemStackHandler
    var newBoard: Boolean
    fun addSingleBounty()
}