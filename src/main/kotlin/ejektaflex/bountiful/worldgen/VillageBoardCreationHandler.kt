package ejektaflex.bountiful.worldgen

import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureVillagePieces
import net.minecraftforge.fml.common.registry.VillagerRegistry
import java.util.*



class VillageBoardCreationHandler : VillagerRegistry.IVillageCreationHandler {
    override fun buildComponent(villagePiece: StructureVillagePieces.PieceWeight?, startPiece: StructureVillagePieces.Start?, pieces: MutableList<StructureComponent>?, random: Random?, x: Int, y: Int, z: Int, facing: EnumFacing?, type: Int): StructureVillagePieces.Village? {
        return VillageBoardComponent.buildComponent(villagePiece, startPiece, pieces, random, x, y, z, facing, type)
    }

    override fun getComponentClass() = VillageBoardComponent::class.java

    override fun getVillagePieceWeight(random: Random, size: Int): StructureVillagePieces.PieceWeight {
        return if (random.nextFloat() <= 0.63f) {
            StructureVillagePieces.PieceWeight(VillageBoardComponent::class.java, 3, 1)
        } else {
            StructureVillagePieces.PieceWeight(VillageBoardComponent::class.java, 0, 0)
        }
    }

}