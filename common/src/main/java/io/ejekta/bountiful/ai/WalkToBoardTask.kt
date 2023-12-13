package io.ejekta.bountiful.ai

import com.google.common.collect.ImmutableMap
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.MemoryModuleState
import net.minecraft.entity.ai.brain.task.LookTargetUtil
import net.minecraft.entity.ai.brain.task.MultiTickTask
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.GlobalPos
import kotlin.jvm.optionals.getOrNull

class WalkToBoardTask(val speed: Float) :
    MultiTickTask<VillagerEntity?>(
        ImmutableMap.of(
            BountifulContent.MEM_MODULE_NEAREST_BOARD,
            MemoryModuleState.VALUE_PRESENT
        ), 1200
    ) {
    override fun shouldRun(serverWorld: ServerWorld, villagerEntity: VillagerEntity?): Boolean {
        return villagerEntity?.brain?.firstPossibleNonCoreActivity?.map { activity: Activity -> activity === Activity.IDLE || activity === Activity.WORK || activity === Activity.PLAY }
            ?.orElse(true) ?: true
    }

    override fun shouldKeepRunning(serverWorld: ServerWorld?, entity: VillagerEntity?, l: Long): Boolean {
        val checked = entity?.brain?.getOptionalMemory(BountifulContent.MEM_MODULE_RECENTLY_CHECKED_BOARD)?.getOrNull() ?: false
        val goalSpot = entity?.brain?.getOptionalMemory(BountifulContent.MEM_MODULE_NEAREST_BOARD)?.getOrNull()

        goalSpot?.let { globalPos ->
            val dist = entity.blockPos.toCenterPos().distanceTo(globalPos.pos.toCenterPos())
            println("Keep going?: $dist")
            if (dist < 2) {
                println("Close enough")
                return false
            }
        }

        return !checked // run until checked
    }

    override fun keepRunning(serverWorld: ServerWorld?, entity: VillagerEntity?, l: Long) {
        if (serverWorld != null && entity != null) {
            LookTargetUtil.walkTowards(
                entity, entity.brain.getOptionalRegisteredMemory(
                    BountifulContent.MEM_MODULE_NEAREST_BOARD
                ).get().pos,
                speed, 1
            )
        }
    }

    override fun finishRunning(serverWorld: ServerWorld?, entity: VillagerEntity?, l: Long) {
        if (serverWorld != null && entity != null) {
            val optional = entity.brain.getOptionalRegisteredMemory(BountifulContent.MEM_MODULE_NEAREST_BOARD)
            optional.ifPresent { pos: GlobalPos ->
                entity.brain.remember(BountifulContent.MEM_MODULE_RECENTLY_CHECKED_BOARD, true)
            }
            entity.brain.forget(BountifulContent.MEM_MODULE_NEAREST_BOARD)
            serverWorld.sendEntityStatus(entity, EntityStatuses.ADD_VILLAGER_HAPPY_PARTICLES)
            serverWorld.playSound(entity, entity.blockPos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1f, 1f)
            // Will only show for a second, but that's okay
            val itemStack = ItemStack(Items.FERN)
            entity.inventory.addStack(itemStack)
            entity.setStackInHand(Hand.MAIN_HAND, itemStack)
        }
    }




    companion object {
        private const val RUN_TIME = 1200

    }
}
