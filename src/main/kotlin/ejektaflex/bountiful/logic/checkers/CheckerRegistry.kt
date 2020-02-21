package ejektaflex.bountiful.logic.checkers

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.data.ValueRegistry
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

    fun passAllChecks(player: PlayerEntity, data: IBountyData, inv: NonNullList<ItemStack>) {
        val checkers = content.map {
            val inst = it.createInstance()
            inst.initialize(player, data, inv)
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


}