package io.ejekta.bountiful.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.ejekta.bountiful.content.MixinHelper;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerGoalPackages.class)
public class BountifulTaskMixin {
    @Inject(method = "getIdlePackage", cancellable = true, at = @At("RETURN"))
    private static void bo_getIdleTasks(float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(speed, cir);
    }

    @Inject(method = "getWorkPackage", cancellable = true, at = @At("RETURN"))
    private static void bo_getWorkTasks(Holder<VillagerProfession> profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(speed, cir);
    }

    @Inject(method = "getRestPackage", cancellable = true, at = @At("RETURN"))
    private static void bo_getRestTasks(float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(speed, cir);
    }
}
