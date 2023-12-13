package io.ejekta.bountiful.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import io.ejekta.bountiful.content.MixinHelper;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTaskListProvider;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerTaskListProvider.class)
public class BountifulTaskMixin {
    @Inject(method = "createIdleTasks", cancellable = true, at = @At("RETURN"))
    private static void bo_getIdleTasks(VillagerProfession profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(profession, speed, cir);
    }

    @Inject(method = "createWorkTasks", cancellable = true, at = @At("RETURN"))
    private static void bo_getWorkTasks(VillagerProfession profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(profession, speed, cir);
    }

    @Inject(method = "createRestTasks", cancellable = true, at = @At("RETURN"))
    private static void bo_getRestTasks(VillagerProfession profession, float speed, CallbackInfoReturnable<ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>>> cir) {
        MixinHelper.INSTANCE.injectNewTasks(profession, speed, cir);
    }
}
