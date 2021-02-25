package io.ejekta.kambrik.templating.block.entity

import io.ejekta.bountiful.content.board.BoardBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Used for blocks which save their NBT data into their respective itemstack
 */
interface IBlockEntityDropSaved {

    fun getItemToSaveTo(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?): ItemStack

    fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        if (pos == null) return
        val be = world?.getBlockEntity(pos) as? BoardBlockEntity ?: return
        val stack = getItemToSaveTo(world, pos, state, player).apply {
            if (tag == null) {
                tag = CompoundTag()
            }
            tag!!.put("BlockEntityTag", be.toTag(CompoundTag()))
        }
        val entity = ItemEntity(
            world,
            player?.pos?.x ?: pos.x.toDouble(),
            player?.pos?.y ?: pos.y.toDouble(),
            player?.pos?.z ?: pos.z.toDouble(),
            stack
        ).apply {
            setToDefaultPickupDelay()
        }
        world.spawnEntity(entity)
    }

}