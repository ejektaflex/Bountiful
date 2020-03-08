package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class BlockBountyBoard() : Block(
        Properties.create(Material.WOOD)
                .sound(SoundType.WOOD)

                .hardnessAndResistance(
                        if (BountifulConfig.SERVER.bountyBoardBreakable.get()) 2f else -1f
                )


) {

    override fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): Boolean {
        if (!worldIn.isRemote) {


            if (!player.isSneaking) {

                val holding = player.getHeldItem(handIn)

                if (BountifulConfig.SERVER.cashInAtBountyBoard.get() && holding.item is ItemBounty) {
                    (holding.item as ItemBounty).cashIn(player, handIn)
                } else {
                    val te = worldIn.getTileEntity(pos)
                    if (te is INamedContainerProvider) {
                        NetworkHooks.openGui(player as ServerPlayerEntity, te as INamedContainerProvider, te.pos)
                    }
                }
            }


        }
        return true
    }

    // Will always have a tile entity
    override fun hasTileEntity(state: BlockState?): Boolean {
        return true
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return BoardTileEntity()
    }


}