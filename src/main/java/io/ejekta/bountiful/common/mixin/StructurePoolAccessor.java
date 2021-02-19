package io.ejekta.bountiful.common.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StructurePool.class)
public interface StructurePoolAccessor {
    @Accessor("elements")
    List<StructurePoolElement> bo_getStructureElements();

    @Accessor("elementCounts")
    List<Pair<StructurePoolElement, Integer>> bo_getStructureCounts();
}
