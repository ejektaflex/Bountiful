package ejektaflex.bountiful.worldgen

import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece
import net.minecraftforge.fml.common.ObfuscationReflectionHelper


object JigsawHelper {

    fun registerJigsaw(server: MinecraftServer, nbtLocation: ResourceLocation, poolLocation: ResourceLocation, weight: Int = 10_000) {

        val pool = server.func_244267_aX()
            .getRegistry(Registry.JIGSAW_POOL_KEY).entries
            .find { it.key.location.toString() == poolLocation.toString() }?.value

        val pieceList = ObfuscationReflectionHelper
            .getPrivateValue<ArrayList<JigsawPiece>, JigsawPattern>(
                JigsawPattern::class.java,
                pool,
                "field_214953_e"
            ) as MutableList<JigsawPiece>

        val piece = JigsawPiece.func_242849_a(nbtLocation.toString()).apply(PlacementBehaviour.RIGID)

        repeat(weight) {
            pieceList.add(piece)
        }

    }

}
