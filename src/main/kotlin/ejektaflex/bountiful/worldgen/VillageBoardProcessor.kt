package ejektaflex.bountiful.worldgen

import net.minecraft.world.gen.structure.template.BlockRotationProcessor
import net.minecraft.world.gen.structure.template.PlacementSettings
import net.minecraft.world.gen.structure.template.Template
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;

class VillageBoardProcessor : BlockRotationProcessor {

    private var pathState: IBlockState
    private var planksState: IBlockState
    private var gravelState: IBlockState
    private var cobbleState: IBlockState

    constructor(pos: BlockPos, settings: PlacementSettings, path: IBlockState, planks: IBlockState, gravel: IBlockState, cobble: IBlockState) : super(pos, settings) {
        this.pathState = path
        this.planksState = planks
        this.gravelState = gravel
        this.cobbleState = cobble
    }

    override fun processBlock(world: World, initialpos: BlockPos, blockInfo: Template.BlockInfo): Template.BlockInfo? {
        if (blockInfo.blockState.getBlock() == Blocks.GRASS_PATH) {
            //Skip this path block in structure gen, substitute our own block 
            //Weird way to handle it, but it should work
            //Based on StructureVillagePieces$Path.addComponentParts

            var pos = initialpos

            while (pos.getY() >= world.getSeaLevel() - 1) {
                val state = world.getBlockState(pos)
                val block = state.getBlock()

                if (block == Blocks.GRASS || block == Blocks.DIRT) {
                    //Need to remove the block above the path to avoid it turning to dirt
                    world.setBlockToAir(initialpos.up())
                    world.setBlockState(initialpos, this.pathState)
                    break
                }
                else if (state.getMaterial().isLiquid()) {
                    world.setBlockState(initialpos, this.planksState)
                    break
                }
                else if (block == Blocks.SAND || block == Blocks.SANDSTONE || block == Blocks.RED_SANDSTONE) {
                    //Slight deviation from vanilla to prevent the structure from replacing blocks it shouldn't
                    val lowerState = world.getBlockState(initialpos.down())
                    if (lowerState.getBlock() == Blocks.AIR || lowerState.getMaterial().isLiquid()) {
                        world.setBlockState(initialpos, this.cobbleState)
                    }
                    else {
                        world.setBlockState(initialpos, this.gravelState)
                    }
                    break
                }

                pos = pos.down()
            }

            //Do not add the original path even if no suitable match was found
            return null
        }
        return super.processBlock(world, initialpos, blockInfo)
    }
}