package ejektaflex.bountiful.worldgen

import com.google.common.collect.ImmutableList
import net.minecraft.client.Minecraft
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece
import net.minecraft.world.gen.feature.template.ProcessorLists
import net.minecraftforge.fml.common.ObfuscationReflectionHelper

/**
 * @author pau101, Paul Fulham. All credit for this class goes to him!
 * Original file: https://gist.github.com/pau101/7d2beb32fd77fe9a870d48ac9b81d862
 * Modified slightly for Kotlin adaptation.
 */

// TODO reimplement village generation

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
