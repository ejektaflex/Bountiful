package io.ejekta.bountiful.common.content

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class BountyBoard : Block(
    FabricBlockSettings.of(Material.WOOD).hardness(0.4f)
), BlockEntityProvider {

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

            }

        }

        return super.onUse(state, world, pos, player, hand, hit)
    }

    override fun createBlockEntity(world: BlockView?): BlockEntity? {
        return BoardBlockEntity()
    }

}