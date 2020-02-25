package ejektaflex.bountiful.block

import ejektaflex.bountiful.BountifulMod
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockReader
import net.minecraft.world.World

class BlockBountyBoard() : Block(
        Properties.create(Material.WOOD)
                .sound(SoundType.WOOD)
                .hardnessAndResistance(
                        if (BountifulMod.config.bountyBoardBreakable) 2f else -1f
                )
) {


    override fun onBlockPlacedBy(world: World, pos: BlockPos, state: BlockState, entity: LivingEntity?, stack: ItemStack) {

        super.onBlockPlacedBy(world, pos, state, entity, stack)

    }

    // Will always have a tile entity
    override fun hasTileEntity(state: BlockState?): Boolean {
        return true
    }

    override fun createTileEntity(state: BlockState?, world: IBlockReader?): TileEntity? {
        return BountyTE()
    }


    /*
    abstract val tileEntityClass: Class<TE>

    @Suppress("UNCHECKED_CAST")
    fun getTileEntity(world: World, pos: BlockPos): TE? {
        return world.getTileEntity(pos) as? TE
    }

     */


    /*
    override fun hasTileEntity(state: IBlockState): Boolean {
        return true
    }

    abstract override fun createTileEntity(world: World, state: IBlockState): TE


     */
}