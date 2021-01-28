package io.ejekta.bountiful.common.mixin;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LongArrayTag.class)
public interface MutableLongArrayTag {
    @Accessor("value")
    long[] getItems();
}
