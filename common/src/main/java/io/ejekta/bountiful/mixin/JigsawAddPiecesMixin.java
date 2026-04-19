package io.ejekta.bountiful.mixin;

import io.ejekta.bountiful.content.JigsawBountyHelper;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(JigsawPlacement.class)
public class JigsawAddPiecesMixin {

    // Targets the private static addPieces overload that actually runs the BFS
    @Inject(
        method = "addPieces(Lnet/minecraft/world/level/levelgen/RandomState;IZLnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Ljava/util/List;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V",
        at = @At("HEAD")
    )
    private static void bountiful_addPiecesHead(
        RandomState randomState, int maxDepth, boolean doExpansionHack,
        ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager,
        LevelHeightAccessor heightAccessor, RandomSource random,
        Registry<StructureTemplatePool> pools, PoolElementStructurePiece centerPiece,
        List<PoolElementStructurePiece> pieces, VoxelShape shape,
        PoolAliasLookup poolAliasLookup, LiquidSettings liquidSettings,
        CallbackInfo ci
    ) {
        JigsawBountyHelper.INSTANCE.onAddPiecesStart(centerPiece);
    }

    @Inject(
        method = "addPieces(Lnet/minecraft/world/level/levelgen/RandomState;IZLnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/Registry;Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Ljava/util/List;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V",
        at = @At("RETURN")
    )
    private static void bountiful_addPiecesReturn(
        RandomState randomState, int maxDepth, boolean doExpansionHack,
        ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager,
        LevelHeightAccessor heightAccessor, RandomSource random,
        Registry<StructureTemplatePool> pools, PoolElementStructurePiece centerPiece,
        List<PoolElementStructurePiece> pieces, VoxelShape shape,
        PoolAliasLookup poolAliasLookup, LiquidSettings liquidSettings,
        CallbackInfo ci
    ) {
        JigsawBountyHelper.INSTANCE.onAddPiecesEnd();
    }
}
