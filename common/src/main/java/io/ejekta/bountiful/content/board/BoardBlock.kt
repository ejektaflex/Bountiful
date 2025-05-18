package io.ejekta.bountiful.content.board

import com.mojang.serialization.MapCodec
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.item.BountyItem
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult


class BoardBlock : BaseEntityBlock(
    Properties.of().sound(SoundType.WOOD).destroyTime(3f).explosionResistance(3600000f)
), EntityBlock {

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState?,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return boardTicker(level, type, BountifulContent.BOARD_ENTITY)
    }

    override fun getDrops(pState: BlockState, pParams: LootParams.Builder): MutableList<ItemStack> {
        val blockEntity = pParams.getParameter(LootContextParams.BLOCK_ENTITY) ?: return mutableListOf()
        if (blockEntity.type == BountifulContent.BOARD_ENTITY) {
            return mutableListOf(ItemStack(BountifulContent.BOARD_ITEM).let {
                if (it.item == BountifulContent.BOARD_ITEM) {
                    it.apply {
                        val regAcc = blockEntity.level?.registryAccess()
                        this.set(DataComponents.CUSTOM_DATA, CustomData.of(blockEntity.saveCustomOnly(regAcc)))
                    }
                } else {
                    it
                }
            })
        }
        return mutableListOf()
    }

    override fun setPlacedBy(
        world: Level?,
        pos: BlockPos?,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        super.setPlacedBy(world, pos, state, placer, itemStack)
        if (world != null && pos != null && itemStack != null && !world.isClientSide) {
            val blockEntity = world.getBlockEntity(pos, BountifulContent.BOARD_ENTITY)
            blockEntity.ifPresent {
                val oldTag = itemStack.get(DataComponents.CUSTOM_DATA)?.copyTag()
                oldTag?.let { old ->
                    it.loadCustomOnly(old, world.registryAccess())
                    it.setChanged()
                }
            }
        }
    }

    // Refuse to break the block if the config disallows it
    override fun getDestroyProgress(
        state: BlockState?,
        player: Player?,
        world: BlockGetter?,
        pos: BlockPos?
    ): Float {
        return if (BountifulIO.configData.board.canBreak) {
            super.getDestroyProgress(state, player, world, pos)
        } else {
            0.0f
        }
    }

    override fun codec(): MapCodec<out BaseEntityBlock> {
        return simpleCodec { _ -> BoardBlock() }
    }

    override fun useItemOn(
        stack: ItemStack?,
        state: BlockState?,
        world: Level,
        pos: BlockPos,
        player: Player?,
        hand: InteractionHand,
        hit: BlockHitResult?
    ): ItemInteractionResult {
        (player as? ServerPlayer)?.let {
            if (!it.isShiftKeyDown) {
                val holding = it.getItemInHand(hand)

                if (holding.item is BountyItem) {
                    //val data = BountyData[holding]
                    val boardEntity = it.level().getBlockEntity(pos) as? BoardBlockEntity ?: return ItemInteractionResult.FAIL
                    val success = (holding.item as BountyItem).tryCashIn(it, holding)
                    if (success) {
                        boardEntity.updateUponBountyCompletion(it, BountyStack(holding))
                        holding.shrink(holding.maxStackSize) // delete bounty only after updating completion
                        boardEntity.setChanged()
                        return ItemInteractionResult.CONSUME
                    }
                } else {
                    val menu = state!!.getMenuProvider(world, pos)

                    if (menu != null) {
                        it.openMenu(menu)
                        return ItemInteractionResult.sidedSuccess(true)
                    }
                }
            }
        }
        return ItemInteractionResult.sidedSuccess(true)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BoardBlockEntity(pos, state)
    }

    companion object {
        const val BOUNTY_SIZE = 24

        private fun <T : BlockEntity?> boardTicker(
            level: Level,
            givenType: BlockEntityType<T>?,
            expectedType: BlockEntityType<out BoardBlockEntity?>?
        ): BlockEntityTicker<T>? {
            //return world.isClient ? null : AbstractFurnaceBlock.validateTicker(givenType, expectedType, AbstractFurnaceBlockEntity::tick);
            return if (level.isClientSide) {
                null
            } else {
                createTickerHelper(givenType, expectedType, BoardBlockEntity::tick)
            }
        }
    }

}



