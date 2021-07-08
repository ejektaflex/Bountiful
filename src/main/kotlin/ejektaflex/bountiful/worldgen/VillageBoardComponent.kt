package ejektaflex.bountiful.worldgen

import ejektaflex.bountiful.BountifulInfo
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureVillagePieces
import net.minecraft.world.gen.structure.template.PlacementSettings
import net.minecraft.world.gen.structure.template.Template
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

class VillageBoardComponent : StructureVillagePieces.Village {

    // Blank default to make Forge happy
    constructor()

    private var boardRotation = Rotation.NONE
    private var boardShift = BlockPos(0,0,0)

    constructor(start: StructureVillagePieces.Start, type: Int, boundingBox: StructureBoundingBox, facing: EnumFacing) : super(start, type) {
        this.boundingBox = boundingBox
        this.setCoordBaseMode(facing)

        //Template rotation and position tweaking
        if (facing != null) {
            when (facing) {
                EnumFacing.SOUTH -> {
                    this.boardRotation = Rotation.CLOCKWISE_180
                    this.boardShift = BlockPos(6,0,6)
                }
                EnumFacing.WEST -> {
                    this.boardRotation = Rotation.COUNTERCLOCKWISE_90
                    this.boardShift = BlockPos(0,0,6)
                }
                EnumFacing.EAST -> {
                    this.boardRotation = Rotation.CLOCKWISE_90
                    this.boardShift = BlockPos(6,0,0)
                }
                else -> {
                    this.boardRotation = Rotation.NONE
                    this.boardShift = BlockPos(0,0,0)
                }
            }
        }
    }

    override fun addComponentParts(world: World, random: Random, boundingBox: StructureBoundingBox): Boolean {
        if (averageGroundLvl < 0) {
            averageGroundLvl = getAverageGroundLevel(world, boundingBox)
            if (averageGroundLvl < 0) {
                return true
            }
            this.boundingBox.offset(0, averageGroundLvl - this.boundingBox.minY - 1, 0)
        }
        val pathState = this.getBiomeSpecificBlockState(Blocks.GRASS_PATH.getDefaultState())
        val planksState = this.getBiomeSpecificBlockState(Blocks.PLANKS.getDefaultState())
        val gravelState = this.getBiomeSpecificBlockState(Blocks.GRAVEL.getDefaultState())
        val cobbleState = this.getBiomeSpecificBlockState(Blocks.COBBLESTONE.getDefaultState())
        val pos = BlockPos(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ)
        val templateManager = world.saveHandler.structureTemplateManager
        val settings = PlacementSettings().setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(boundingBox).setRotation(this.boardRotation)  
        val processor = VillageBoardProcessor(pos, settings, pathState, planksState, gravelState, cobbleState)

        val template = templateManager.getTemplate(world.minecraftServer, getTemplateResourceLocation())

        template.addBlocksToWorld(world, pos.add(this.boardShift), processor, settings, 2)
        return true
    }

    private fun getTemplateResourceLocation() : ResourceLocation {
        when(this.structureType) {
            1 -> return VILLAGE_BOARD_ID1
            2 -> return VILLAGE_BOARD_ID2
            3 -> return VILLAGE_BOARD_ID3
            else -> return VILLAGE_BOARD_ID
        }
    }

    companion object {
        val VILLAGE_BOARD_ID = ResourceLocation(BountifulInfo.MODID, "village_board")
        val VILLAGE_BOARD_ID1 = ResourceLocation(BountifulInfo.MODID, "village_board_desert")
        val VILLAGE_BOARD_ID2 = ResourceLocation(BountifulInfo.MODID, "village_board_savanna")
        val VILLAGE_BOARD_ID3 = ResourceLocation(BountifulInfo.MODID, "village_board_taiga")

        fun buildComponent(villagePiece: StructureVillagePieces.PieceWeight?, startPiece: StructureVillagePieces.Start?, pieces: List<StructureComponent>?, random: Random?, x: Int, y: Int, z: Int, facing: EnumFacing?, type: Int): StructureVillagePieces.Village? {
            val boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 7, 6, 7, facing!!)
            return if (StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null) {
                VillageBoardComponent(startPiece!!, type, boundingBox, facing)
            } else null
        }
    }
}
