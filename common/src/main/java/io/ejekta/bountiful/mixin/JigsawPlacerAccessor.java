package io.ejekta.bountiful.mixin;

import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public interface JigsawPlacerAccessor {
    @SuppressWarnings("unchecked")
    @Accessor("pieces")
    List<PoolElementStructurePiece> bountiful_getPieces();
}
