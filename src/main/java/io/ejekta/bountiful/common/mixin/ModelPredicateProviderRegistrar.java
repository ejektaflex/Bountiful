package io.ejekta.bountiful.common.mixin;

import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ModelPredicateProviderRegistry.class)
public interface ModelPredicateProviderRegistrar {

    @Invoker("register")
    public static void registerInvoker(Item item, Identifier id, ModelPredicateProvider provider) {
        throw new AssertionError();
    }

}
