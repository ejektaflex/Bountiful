package bountiful.gui

import bountiful.block.TileEntityBountyBoard
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiHandler : IGuiHandler {

    override fun getClientGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (id == BOARD_GUI) {
            return GuiBoard(ContainerBoard(player!!.inventory, world!!.getTileEntity(BlockPos(x, y, z)) as TileEntityBountyBoard ), player.inventory)
        }
        println("Did NOT get Client Gui.")
        return null
    }

    override fun getServerGuiElement(id: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        if (id == BOARD_GUI) {
            return ContainerBoard(player!!.inventory, world!!.getTileEntity(BlockPos(x, y, z)) as TileEntityBountyBoard )
        }
        println("Did NOT get Server Gui")
        return null
    }


    companion object {
        val BOARD_GUI = 9336
    }

}