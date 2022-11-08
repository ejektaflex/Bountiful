package io.ejekta.bountiful.mixin;

import io.ejekta.bountiful.advancement.CriterionHelper;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(AbstractCriterion.class)
public class AbstractCriterionTriggerer<T extends AbstractCriterionConditions> {
    @Inject(method = "trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Predicate;)V",
        at = @At("HEAD")
    )
    private void injected(ServerPlayerEntity player, Predicate<T> predicate, CallbackInfo ci) {
        CriterionHelper.INSTANCE.handle(player, (AbstractCriterion<T>)(Object)this, predicate);
    }
}
