package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.data.ValueRegistry
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.player.PlayerEntity
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object CheckerRegistry : ValueRegistry<KClass<out CheckHandler<*>>>() {

    init {
        add(StackLikeCheckHandler::class)
        add(EntityCheckHandler::class)
    }

    fun tryCashIn(player: PlayerEntity, data: IBountyData) {
        val checkers = content.map {
            val inst = it.createInstance()
            inst.initialize(player, data)
            inst
        }

        println("Checkers: $checkers")
        val passesAll = checkers.all { it.isComplete }
        println("Passes all checks?: $passesAll")

        if (passesAll) {
            checkers.forEach {
                it.fulfill()
            }
            for (reward in data.rewards.content) {
                (reward as IBountyReward).reward(player)
            }
        }
    }

    fun passedChecks(player: PlayerEntity, data: IBountyData): Map<BountyEntry, BountyProgress> {
        val checkers = content.map {
            val inst = it.createInstance()
            inst.initialize(player, data)
            inst
        }

        return checkers.map {
            it.objectiveStatus().toMutableMap()
        }.reduce { mapA, mapB ->
            mapA.apply {
                putAll(mapB)
            }
        }
    }


}