package io.ejekta.bountiful.content

import com.google.common.collect.ImmutableList
import io.ejekta.bountiful.ai.WalkToBoardTask
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask
import net.minecraft.entity.ai.brain.task.Task
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.village.VillagerProfession
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import java.util.*
import com.mojang.datafixers.util.Pair as MojangPair

object MixinHelper {

    fun injectNewTasks(
        profession: VillagerProfession,
        speed: Float,
        cir: CallbackInfoReturnable<ImmutableList<MojangPair<Int, out Task<in VillagerEntity?>?>>>
    ) {
        val options = cir.returnValue.toMutableList()
        options.add(
            MojangPair(
                2,
                WalkToBoardTask(0.7f)
            )
        )
        cir.setReturnValue(ImmutableList.copyOf(options))
    }
}