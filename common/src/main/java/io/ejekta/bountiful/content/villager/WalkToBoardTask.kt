package io.ejekta.bountiful.content.villager

import com.google.common.collect.ImmutableMap
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.behavior.Behavior
import net.minecraft.world.entity.ai.behavior.BehaviorUtils
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.schedule.Activity
import kotlin.jvm.optionals.getOrNull

class WalkToBoardTask(val speed: Float) :
    Behavior<Villager>(
        ImmutableMap.of(
            BountifulContent.MEM_MODULE_NEAREST_BOARD,
            MemoryStatus.VALUE_PRESENT
        ), 1200
    ) {
    override fun checkExtraStartConditions(serverWorld: ServerLevel, villagerEntity: Villager): Boolean {
        return villagerEntity.brain.activeNonCoreActivity.map {
            activity: Activity -> activity === Activity.IDLE || activity === Activity.WORK || activity === Activity.PLAY
        }.orElse(true)
    }

    override fun canStillUse(serverWorld: ServerLevel, entity: Villager, l: Long): Boolean {
        val goalSpot = entity.brain.getMemoryInternal(BountifulContent.MEM_MODULE_NEAREST_BOARD)?.getOrNull()

        goalSpot?.let { globalPos ->
            val dist = entity.blockPosition().center.distanceTo(globalPos.pos.center)
            if (dist < 1.75) {
                println("Close enough")
                return false
            }
        }

        return true
    }

    override fun tick(serverWorld: ServerLevel, entity: Villager, l: Long) {
        val memPos = entity.brain.getMemory(BountifulContent.MEM_MODULE_NEAREST_BOARD).getOrNull()?.pos
        memPos?.let {
            BehaviorUtils.setWalkAndLookTargetMemories(
                entity, it,
                speed, 1
            )
        }
    }

    override fun stop(serverWorld: ServerLevel, entity: Villager, l: Long) {
        val goalSpot = entity.brain.getMemoryInternal(BountifulContent.MEM_MODULE_NEAREST_BOARD)?.getOrNull()

        goalSpot?.let {
            val boardEntity = serverWorld.getBlockEntity(it.pos, BountifulContent.BOARD_ENTITY).getOrNull()
            boardEntity?.handleVillagerVisit(entity)
        }
        entity.brain.eraseMemory(BountifulContent.MEM_MODULE_NEAREST_BOARD)
    }




    companion object {
        private const val RUN_TIME = 1200
    }
}
