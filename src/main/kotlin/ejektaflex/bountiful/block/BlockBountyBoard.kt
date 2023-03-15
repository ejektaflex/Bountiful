package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class BlockBountyBoard : Block(
        //Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(10f)
    Properties.of(Material.WOOD)
) {


    override fun use(
        state: BlockState,
        levelIn: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!levelIn.isClientSide) {

            if (!player.isCrouching) {
                val holding = player.getItemInHand(hand)

                if (BountifulConfig.SERVER.cashInAtBountyBoard.get() && holding.item is ItemBounty) {
                    (holding.item as ItemBounty).cashIn(player, hand)
                } else {
                    val be = levelIn.getBlockEntity(pos)
                    if (be is MenuProvider) {
                        NetworkHooks.openScreen(player as ServerPlayer, be, be.blockPos)
                    }
                }
            }

        }
        return InteractionResult.SUCCESS
    }


// TODO reimplement bounty board block harvesting
//    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
//
//        val te = worldIn.getTileEntity(pos)
//
//        if (!worldIn.isRemote && te is BoardBlockEntity) {
//            // Create stack and serialize data
//            val stack = ItemStack(BountifulContent.BOUNTYBOARD)
//            stack.setTagInfo("BlockEntityTag", te.serializeNBT())
//
//            // Throw it on the ground
//            val entity = ItemEntity(
//                    worldIn,
//                    pos.x.toDouble(),
//                    pos.y.toDouble(),
//                    pos.z.toDouble(),
//                    stack
//            ).apply {
//                setDefaultPickupDelay()
//            }
//
//            worldIn.addEntity(entity)
//        }
//
//        super.onBlockHarvested(worldIn, pos, state, player)
//    }



    // Will always have a tile entity
    override fun hasTileEntity(state: BlockState?): Boolean {
        return true
    }



    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity {
        return BoardBlockEntity()
    }


}