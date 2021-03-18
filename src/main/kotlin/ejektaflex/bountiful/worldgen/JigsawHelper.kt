package ejektaflex.bountiful.worldgen

import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece
import net.minecraft.world.gen.feature.template.ProcessorLists
import net.minecraftforge.fml.common.ObfuscationReflectionHelper


object JigsawHelper {

    fun registerJigsaw(server: MinecraftServer, nbtLocation: ResourceLocation, poolLocation: ResourceLocation, processorLocation: ResourceLocation, weight: Int = 10_000) {

        val pool = server.func_244267_aX()
            .getRegistry(Registry.JIGSAW_POOL_KEY).entries
            .find { it.key.location.toString() == poolLocation.toString() }?.value


        var processorLists = server.func_244267_aX()
                .getRegistry(Registry.STRUCTURE_PROCESSOR_LIST_KEY)
                .getOptional(processorLocation)
                .orElse(ProcessorLists.field_244101_a) // minecraft:empty

        val pieceList = ObfuscationReflectionHelper
            .getPrivateValue<ArrayList<JigsawPiece>, JigsawPattern>(
                JigsawPattern::class.java,
                pool,
                "field_214953_e"
            ) as MutableList<JigsawPiece>

        val piece = JigsawPiece.func_242851_a(nbtLocation.toString(), processorLists).apply(PlacementBehaviour.RIGID)

        repeat(weight) {
            pieceList.add(piece)
        }

    }

}
