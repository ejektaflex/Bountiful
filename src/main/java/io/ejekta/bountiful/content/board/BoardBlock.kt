package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyItem
import io.ejekta.kambrik.templating.block.entity.IBlockEntityDropSaved
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World


class BoardBlock : BlockWithEntity(
    FabricBlockSettings.of(Material.WOOD).hardness(5f).resistance(3600000f)
), BlockEntityProvider, IBlockEntityDropSaved {

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getItemToSaveTo(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?): ItemStack {
        return ItemStack(BountifulContent.BOARD)
    }

    override fun <T : BlockEntity> getTicker(
        world: World?,
        state: BlockState?,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        //return super.getTicker(world, state, type)
        //return world.isClient ? null : checkType(type, BlockEntityType.BREWING_STAND, BrewingStandBlockEntity::tick);
        return if (world?.isClient == true) {
            null
        } else {
            checkType(type, BountifulContent.BOARD_ENTITY, BoardBlockEntity::tick)
        }
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        super<BlockWithEntity>.onBreak(world, pos, state, player)
        super<IBlockEntityDropSaved>.onBreak(world, pos, state, player)
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult?
    ): ActionResult {

        if (world?.isClient == false) {

            if (!player.isSneaking) {

                val holding = player.getStackInHand(hand)

                if (holding.item is BountyItem) {
                    val data = BountyData[holding]
                    val success = data.tryCashIn(player, holding)

                    if (success) {
                        val bountyEntity = world.getBlockEntity(pos) as? BoardBlockEntity ?: return ActionResult.FAIL
                        bountyEntity.updateCompletedBounties(player)
                        bountyEntity.markDirty()
                        return ActionResult.success(true)
                    }

                } else {
                    val screenHandlerFactory = state!!.createScreenHandlerFactory(world, pos)
                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory)
                        return ActionResult.success(false)
                    }
                }

            }

        }

        return ActionResult.FAIL
    }

    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity {
        return BoardBlockEntity()
    }

    companion object {
        const val BOUNTY_SIZE = 24
    }

}



