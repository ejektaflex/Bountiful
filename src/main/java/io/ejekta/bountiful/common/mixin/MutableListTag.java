package io.ejekta.bountiful.common.mixin;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(ListTag.class)
public interface MutableListTag {
    @Accessor("value")
    List<Tag> getItems();
}
