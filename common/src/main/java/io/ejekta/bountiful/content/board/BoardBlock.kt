package io.ejekta.bountiful.content.board

import com.mojang.serialization.MapCodec
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyInfo
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.item.BountyItem
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World


class BoardBlock : BlockWithEntity(
    Settings.create().sounds(BlockSoundGroup.WOOD).hardness(3f).resistance(3600000f)
), BlockEntityProvider {

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState?,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return boardTicker(world, type, BountifulContent.BOARD_ENTITY)
    }

    override fun getDroppedStacks(
        state: BlockState,
        builder: LootContextParameterSet.Builder
    ): MutableList<ItemStack> {
        val blockEntity = builder.get(LootContextParameters.BLOCK_ENTITY) ?: return mutableListOf()
        if (blockEntity.type == BountifulContent.BOARD_ENTITY) {
            return super.getDroppedStacks(state, builder).map {
                it.also { blockEntity.setStackNbt(it) }
            }.toMutableList()
        }
        return mutableListOf()
    }

    override fun onPlaced(
        world: World?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (world != null && pos != null && itemStack != null && !world.isClient) {
            val blockEntity = world.getBlockEntity(pos, BountifulContent.BOARD_ENTITY)
            blockEntity.ifPresent {
                val itemNbt = itemStack.nbt ?: return@ifPresent
                it.readNbt(itemNbt)
                it.markDirty()
            }
        }
    }

    // Refuse to break the block if the config disallows it
    override fun calcBlockBreakingDelta(
        state: BlockState?,
        player: PlayerEntity?,
        world: BlockView?,
        pos: BlockPos?
    ): Float {
        return if (BountifulIO.configData.board.canBreak) {
            super.calcBlockBreakingDelta(state, player, world, pos)
        } else {
            0.0f
        }
    }

    override fun getCodec(): MapCodec<out BlockWithEntity> {
        return createCodec { bfs -> BoardBlock() }
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult?
    ): ActionResult {
        (player as? ServerPlayerEntity)?.let {
            if (!it.isSneaking) {
                val holding = it.getStackInHand(hand)

                if (holding.item is BountyItem) {
                    val data = BountyData[holding]
                    val boardEntity = it.world.getBlockEntity(pos) as? BoardBlockEntity ?: return ActionResult.FAIL
                    val success = data.tryCashIn(it, holding)
                    if (success) {
                        boardEntity.updateUponBountyCompletion(it, data, BountyInfo[holding])
                        boardEntity.markDirty()
                        return ActionResult.CONSUME
                    }
                } else {
                    val screenHandlerFactory = state!!.createScreenHandlerFactory(world, pos)
                    if (screenHandlerFactory != null) {
                        it.openHandledScreen(screenHandlerFactory)
                        return ActionResult.success(true)
                    }
                }
            }
        }
        return ActionResult.success(true)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BoardBlockEntity(pos, state)
    }

    companion object {
        const val BOUNTY_SIZE = 24

        private fun <T : BlockEntity?> boardTicker(
            world: World,
            givenType: BlockEntityType<T>?,
            expectedType: BlockEntityType<out BoardBlockEntity?>?
        ): BlockEntityTicker<T>? {
            //return world.isClient ? null : AbstractFurnaceBlock.validateTicker(givenType, expectedType, AbstractFurnaceBlockEntity::tick);
            return if (world.isClient) {
                null
            } else {
                validateTicker(givenType, expectedType, BoardBlockEntity::tick)
            }
        }
    }

}



