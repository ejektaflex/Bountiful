package ejektaflex.bountiful.worldgen

import net.minecraft.server.MinecraftServer
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.util.ObfuscationReflectionHelper


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
