package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulContent
import ejektaflex.bountiful.ext.filledBountySlots
import ejektaflex.bountiful.item.ItemBounty
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.item.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Hand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootParameters
import net.minecraftforge.common.ToolType
import net.minecraftforge.fml.network.NetworkHooks

class BlockBountyBoard() : Block(
        Properties.create(Material.WOOD).sound(SoundType.WOOD)
) {

    val hardness: Float by lazy {
        if (BountifulConfig.SERVER.bountyBoardBreakable.get()) 2f else -1f
    }

    override fun getBlockHardness(blockState: BlockState, worldIn: IBlockReader, pos: BlockPos): Float {
        return hardness
    }

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

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (!worldIn.isRemote && stack.hasTag()) {
            val te = worldIn.getTileEntity(pos)
            if (te is BoardTileEntity) {
                //te.deserializeNBT(stack.tag.get("BoardData"))
            }
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
    }

    override fun onBlockHarvested(worldIn: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {

        val te = worldIn.getTileEntity(pos)

        if (!worldIn.isRemote && te is BoardTileEntity) {
            // Create stack and serialize data
            val stack = ItemStack(BountifulContent.Items.BOUNTYBOARD)
            stack.setTagInfo("BlockEntityTag", te.serializeNBT())

            // Throw it on the ground
            val entity = ItemEntity(
                    worldIn,
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble(),
                    stack
            ).apply {
                setDefaultPickupDelay()
            }

            worldIn.addEntity(entity)
        }

        super.onBlockHarvested(worldIn, pos, state, player)
    }

    /*
    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val te = builder.get(LootParameters.BLOCK_ENTITY)

        if (te is BoardTileEntity) {
            builder.withDynamicDrop(ResourceLocation("minecraft", "contents")) { a, b ->
                for (item in te.handler.filledBountySlots) {
                    b.
                }
            }
        }

        return super.getDrops(state, builder)
    }

     */


    // Will always have a tile entity
    override fun hasTileEntity(state: BlockState?): Boolean {
        return true
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return BoardTileEntity()
    }


}