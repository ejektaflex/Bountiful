package io.ejekta.bountiful.ai

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.mojang.datafixers.kinds.IdF
import com.mojang.datafixers.kinds.OptionalBox
import com.mojang.datafixers.util.Pair
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.MemoryQueryResult
import net.minecraft.entity.ai.brain.task.*
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.GlobalPos
import net.minecraft.village.VillagerProfession
import net.minecraft.world.poi.PointOfInterestType
import net.minecraft.world.poi.PointOfInterestTypes
import java.util.*
import kotlin.jvm.optionals.getOrNull

object VillagerBoardTasks {
    fun createMeetTasks(
        profession: VillagerProfession?,
        speed: Float
    ): ImmutableList<Pair<Int, out Task<out LivingEntity>>> {
        return ImmutableList.of(
            Pair.of(
                2, GoToIfNearbyTask.create(BountifulContent.MEM_MODULE_NEAREST_BOARD, 0.4f, 40)
            )
        )
    }


    fun scramble(villager: VillagerEntity) {
        villager.brain.doExclusively(BountifulContent.ACT_CHECK_BOARD)
    }



}