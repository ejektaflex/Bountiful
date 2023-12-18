package io.ejekta.bountiful.content

import com.google.common.collect.ImmutableList
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.content.villager.WalkToBoardTask
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask
import net.minecraft.entity.ai.brain.task.Task
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.ItemStack
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.ForgingScreenHandler
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

    fun modifyAnvilResults(handler: AnvilScreenHandler) {
        println(handler)
        val inA = handler.input.getStack(0)
        val inB = handler.input.getStack(1)
        if (!inA.isEmpty && inA.item is DecreeItem && !inB.isEmpty && inB.item is DecreeItem) {
            val dataA = DecreeData[inA]
            val dataB = DecreeData[inB]
            val combined = dataA.copy().apply {
                val newIds = dataB.ids.filter { it !in dataA.ids }
                ids.addAll(newIds)
            }
            val decreeProto = ItemStack(BountifulContent.DECREE_ITEM)
            DecreeData[decreeProto] = combined
            handler.output.setStack(0, decreeProto)
        }
    }
}