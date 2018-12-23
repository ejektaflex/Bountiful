package bountiful.block

import bountiful.Bountiful
import bountiful.BountifulInfo
import bountiful.gui.GuiHandler
import bountiful.item.ItemBounty
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

class BlockBountyBoard : BlockTileEntity<TileEntityBountyBoard>(Material.WOOD, "bountyboard") {

    override val tileEntityClass: Class<TileEntityBountyBoard>
        get() = TileEntityBountyBoard::class.java

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world!!.isRemote) {
            val holding = player?.getHeldItem(hand!!)
            if (!player!!.isSneaking) {
                if (Bountiful.config.cashInAtBountyBoard && holding?.item is ItemBounty) {
                    (holding.item as ItemBounty).cashIn(player, hand!!, atBoard = true)
                } else {
                    player.openGui(Bountiful.instance!!, GuiHandler.BOARD_GUI, world, pos!!.x, pos.y, pos.z)
                }
            }
        }
        return true
    }

    // Initial population of board when placed
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = (getTileEntity(worldIn, pos) as TileEntityBountyBoard)
        for (i in 0 until Bountiful.config.maxBountiesPerBoard / 2) {
            tile.addSingleBounty()
            tile.markDirty()
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntityBountyBoard {
        return TileEntityBountyBoard()
    }

}
