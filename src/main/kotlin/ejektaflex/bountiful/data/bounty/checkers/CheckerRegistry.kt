package ejektaflex.bountiful.data.bounty.checkers

import ejektaflex.bountiful.data.bounty.BountyData
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.data.bounty.BountyProgress
import ejektaflex.bountiful.data.bounty.IBountyReward
import ejektaflex.bountiful.util.ValueRegistry
import net.minecraft.world.entity.player.Player
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object CheckerRegistry : ValueRegistry<KClass<out CheckHandler<*>>>() {

    init {
        add(StackLikeCheckHandler::class)
        add(EntityCheckHandler::class)
    }

    fun tryCashIn(player: Player, data: BountyData): Boolean {
        val checkers = content.map {
            val inst = it.createInstance()
            inst.initialize(player, data)
            inst
        }

        //println("Checkers: $checkers")
        val passesAll = checkers.all { it.isComplete }
        //println("Passes all checks?: $passesAll")

        if (passesAll) {
            checkers.forEach {
                it.fulfill()
            }

            for (reward in data.rewards.content) {
                (reward as IBountyReward).reward(player)
            }

            // Return success
            return true
        }
        return false
    }

    fun passedChecks(player: Player, data: BountyData): Map<BountyEntry, BountyProgress> {
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