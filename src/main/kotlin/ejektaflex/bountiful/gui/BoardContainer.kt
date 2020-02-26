package ejektaflex.bountiful.gui

import ejektaflex.bountiful.block.BoardTE
import ejektaflex.bountiful.content.ModContent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.ContainerType
import net.minecraft.util.IWorldPosCallable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class BoardContainer(id: Int, val world: World, val pos: BlockPos, val inv: PlayerInventory) : Container(ModContent.Guis.BOARDCONTAINER, id) {

    val boardTE: BoardTE by lazy {
        world.getTileEntity(pos) as BoardTE
    }

    /*
    val boardCap: IItemHandler by lazy {
        boardTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
    }

     */

    // TODO Maybe use isWithinUsableDistance later on
    override fun canInteractWith(playerIn: PlayerEntity): Boolean {
        return true
    }

}