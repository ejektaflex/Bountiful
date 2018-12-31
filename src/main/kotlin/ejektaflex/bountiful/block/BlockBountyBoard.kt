package ejektaflex.bountiful.block

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.ext.filledSlots
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.api.ext.stacks
import ejektaflex.bountiful.gui.GuiHandler
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

class BlockBountyBoard : BlockTileEntity<TileEntityBountyBoard>(Material.WOOD, "bountyboard") {

    init {
        setHardness(2.0f)
    }

    override val tileEntityClass: Class<TileEntityBountyBoard>
        get() = TileEntityBountyBoard::class.java

    override fun onBlockActivated(world: World?, pos: BlockPos?, state: IBlockState?, player: EntityPlayer?, hand: EnumHand?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (!world!!.isRemote && pos != null) {
            val holding = player?.getHeldItem(hand!!)
            val tile = (world.getTileEntity(pos)!! as TileEntityBountyBoard)
            if (!player!!.isSneaking) {
                // Cash in, or else try get bounty
                if (Bountiful.config.cashInAtBountyBoard && holding?.item is ItemBounty) {
                    (holding.item as ItemBounty).cashIn(player, hand!!, atBoard = true)
                } else if (tile.inventory.handler.filledSlots.isEmpty()) {
                    player.sendMessage("§6No bounties available, come back later!")
                } else {
                    player.openGui(Bountiful.instance!!, GuiHandler.BOARD_GUI, world, pos.x, pos.y, pos.z)
                }
            }
        }
        return true
    }

    override fun getWeakPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        return if (blockAccess is World) {
            val tile = (getTileEntity(blockAccess, pos) as TileEntityBountyBoard)
            if (tile.pulseLeft > 0) 15 else 0
        } else {
            0
        }
    }

    override fun shouldCheckWeakPower(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        return false
    }

    override fun canProvidePower(state: IBlockState): Boolean {
        return true
    }

    override fun hasComparatorInputOverride(state: IBlockState): Boolean {
        return true
    }

    override fun getComparatorInputOverride(blockState: IBlockState, worldIn: World, pos: BlockPos): Int {
        val tile = (getTileEntity(worldIn, pos) as TileEntityBountyBoard)
        return (tile.inventory.handler.filledSlots.size / 2)
    }

    // Initial population of board when placed
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = (getTileEntity(worldIn, pos) as TileEntityBountyBoard)
        for (i in 0 until Bountiful.config.bountiesCreatedOnPlace) {
            tile.inventory.addSingleBounty(worldIn, tile)
            tile.markDirty()
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntityBountyBoard {
        return TileEntityBountyBoard()
    }

}
