package io.ejekta.bountiful.common.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(CompoundTag.class)
public interface MutableCompoundTag {
    @Accessor("tags")
    Map<String, Tag> getAllTags();


}
