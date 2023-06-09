package io.ejekta.bountiful.content.board

import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyItem
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


class BoardBlock : BlockWithEntity(
    Settings.create().sounds(BlockSoundGroup.WOOD).hardness(3f).resistance(3600000f)
), BlockEntityProvider {

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
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

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val stack = BountifulContent.BOARD_ITEM.defaultStack
        val be = world.getBlockEntity(pos) as? BoardBlockEntity ?: return
        be.setStackNbt(stack)
        val itemEntity = ItemEntity(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
        world.spawnEntity(itemEntity)
        super.onBreak(world, pos, state, player)
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
                        return ActionResult.CONSUME
                    }

                } else {
                    val screenHandlerFactory = state!!.createScreenHandlerFactory(world, pos)
                    if (screenHandlerFactory != null) {
                        player.openHandledScreen(screenHandlerFactory)
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
    }

}



