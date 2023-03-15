package ejektaflex.bountiful.worldgen

import com.mojang.serialization.Codec
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.util.Direction
import net.minecraft.core.BlockPos
import net.minecraft.world.ISeedReader
import net.minecraft.world.gen.ChunkGenerator
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.NoFeatureConfig
import net.minecraft.world.gen.feature.jigsaw.FeatureJigsawPiece
import net.minecraftforge.common.extensions.IForgeBlockState
import java.util.*

class BoardFeature(codec: Codec<NoFeatureConfig>, state: BlockState) : Feature<NoFeatureConfig>(codec) {

    override fun generate(
        reader: ISeedReader,
        generator: ChunkGenerator,
        rand: Random,
        pos: BlockPos,
        config: NoFeatureConfig
    ): Boolean {
        val facing = Direction.values().random()
        val state = reader.getBlockState(pos)

        if (
            isDirtAt(reader, pos.north(2))
            && isDirtAt(reader, pos.south(2))
            && isDirtAt(reader, pos.east(2))
            && isDirtAt(reader, pos.west(2))
            && isAirAt(reader, pos.up())
        ) {
            println("BoReg AIR!")



        }

        return true

    }


}