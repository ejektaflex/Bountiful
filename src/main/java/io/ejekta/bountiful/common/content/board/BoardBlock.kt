package io.ejekta.bountiful.common.content.board

import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.content.BountyItem
import io.ejekta.bountiful.common.content.DecreeItem
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World


class BoardBlock : BlockWithEntity(
    FabricBlockSettings.of(Material.WOOD).hardness(0.4f)
), BlockEntityProvider {

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
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

    override fun createBlockEntity(world: BlockView?): BlockEntity {
        return BoardBlockEntity().apply {
            decrees.setStack((0..2).random(), DecreeItem.create())
        }
    }

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        newState: BlockState,
        moved: Boolean
    ) {
        if (state.block !== newState.block) {
            val blockEntity = world.getBlockEntity(pos)
            if (blockEntity is BoardBlockEntity) {
                //ItemScatterer.spawn(world, pos, blockEntity) TO DO redo this!
                // update comparators
                world.updateComparators(pos, this)
            }
            super.onStateReplaced(state, world, pos, newState, moved)
        }
    }

    override fun hasComparatorOutput(state: BlockState?) = true

    override fun getComparatorOutput(state: BlockState?, world: World?, pos: BlockPos?): Int {
        return super.getComparatorOutput(state, world, pos)
    }

    companion object {
        const val BOUNTY_SIZE = 24
    }

}



