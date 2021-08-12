package io.ejekta.bountiful.mixin;

import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(NbtList.class)
public interface MutableListTag {
    @Accessor("value")
    List<NbtElement> getItems();

    @Accessor("type")
    void setTagType(byte newType);

}
