package io.ejekta.bountiful.mixin;

import io.ejekta.bountiful.content.JigsawBountyHelper;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public class JigsawPlacementMixin {

    /**
     * Intercepts the candidate list for each jigsaw house-slot just after it is fully populated
     * (the point right before placementPriority() is read, which is always reached after both
     * addAll calls regardless of depth). We either remove the bounty board entirely or move it
     * to the front so it is tried before any competing house piece.
     */
    @ModifyVariable(
        method = "tryPlacingChildren(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IZLnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$JigsawBlockInfo;placementPriority()I",
            shift = At.Shift.BEFORE
        ),
        ordinal = 0
    )
    private List<StructurePoolElement> bountiful_filterTargetPieces(List<StructurePoolElement> targetPieces) {
        return JigsawBountyHelper.INSTANCE.filterTargetPieces(targetPieces, (JigsawPlacerAccessor)(Object)this);
    }
}
