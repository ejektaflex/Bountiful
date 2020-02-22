package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.data.ValueRegistry
import ejektaflex.bountiful.logic.BountyProgress
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object CheckerRegistry : ValueRegistry<KClass<out CheckHandler<*>>>() {

    init {
        add(StackCheckHandler::class)
        add(EntityCheckHandler::class)
    }

    fun passAllChecks(player: PlayerEntity, data: IBountyData) {
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