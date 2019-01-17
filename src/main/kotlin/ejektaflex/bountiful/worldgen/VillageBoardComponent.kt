package ejektaflex.bountiful.worldgen

import ejektaflex.bountiful.BountifulInfo
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
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

    constructor(start: StructureVillagePieces.Start, type: Int, boundingBox: StructureBoundingBox, facing: EnumFacing) : super(start, type) {
        this.boundingBox = boundingBox
        coordBaseMode = facing
    }

    override fun addComponentParts(world: World, random: Random, boundingBox: StructureBoundingBox): Boolean {
        if (averageGroundLvl < 0) {
            averageGroundLvl = getAverageGroundLevel(world, boundingBox)
            if (averageGroundLvl < 0) {
                return true
            }
            this.boundingBox.offset(0, averageGroundLvl - this.boundingBox.minY - 1, 0)
        }
        val pos = BlockPos(this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ)
        val templateManager = world.saveHandler.structureTemplateManager
        val settings = PlacementSettings().setReplacedBlock(Blocks.STRUCTURE_VOID).setBoundingBox(boundingBox)
        val template = templateManager.getTemplate(world.minecraftServer, VILLAGE_BOARD_ID)
        template.addBlocksToWorldChunk(world, pos, settings)
        return true
    }

    companion object {
        val VILLAGE_BOARD_ID = ResourceLocation(BountifulInfo.MODID, "village_board")

        fun buildComponent(villagePiece: StructureVillagePieces.PieceWeight?, startPiece: StructureVillagePieces.Start?, pieces: List<StructureComponent>?, random: Random?, x: Int, y: Int, z: Int, facing: EnumFacing?, type: Int): StructureVillagePieces.Village? {
            val boundingBox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 3, 3, 3, facing!!)
            return if (StructureVillagePieces.Village.canVillageGoDeeper(boundingBox) && StructureComponent.findIntersecting(pieces, boundingBox) == null) {
                VillageBoardComponent(startPiece!!, type, boundingBox, facing)
            } else null
        }
    }
}
