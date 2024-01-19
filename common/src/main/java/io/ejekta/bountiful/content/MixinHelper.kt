package io.ejekta.bountiful.content

import com.google.common.collect.ImmutableList
import io.ejekta.bountiful.bounty.DecreeData
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.content.villager.WalkToBoardTask
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask
import net.minecraft.entity.ai.brain.task.Task
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.AnvilScreenHandler
import net.minecraft.screen.ForgingScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
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
        val inA = handler.input.getStack(0)
        val inB = handler.input.getStack(1)
        if (!inA.isEmpty && inA.item is DecreeItem && !inB.isEmpty && inB.item is DecreeItem) {
            val dataA = DecreeData[inA]
            val dataB = DecreeData[inB]

            if (dataA.ids == dataB.ids) {
                return
            }

            val combined = dataA.copy().apply {
                ids.addAll(dataB.ids)
            }
            val decreeProto = ItemStack(BountifulContent.DECREE_ITEM)
            DecreeData[decreeProto] = combined
            handler.levelCost.set(combined.ids.size * 3 - 1)
            handler.output.setStack(0, decreeProto)
        }
    }

    fun takeAnvilResults(playerEntity: PlayerEntity, stack: ItemStack, handler: AnvilScreenHandler) {
        // This is really hacky; stack enters as the decree but count of 0, so getItem returns air unless we
        // temporarily increment and then reset. Anvil never seems to use this variable, but we reset it just to be safe.
        val currCount = stack.count
        stack.increment(1)
        if (stack.item is DecreeItem) {
            (playerEntity as? ServerPlayerEntity)?.run { BountifulContent.Triggers.PRINTING_PRESS.trigger(this) }
        }
        stack.count = currCount
    }

}