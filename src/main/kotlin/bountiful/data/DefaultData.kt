package bountiful.data

import bountiful.logic.pickable.PickableEntry

object DefaultData {
    val bounties = listOf(
            PickableEntry("minecraft:dirt", 16..128, 5),
            PickableEntry("minecraft:stone", 16..128, 10),
            PickableEntry("minecraft:cobblestone", 16..128, 7),
            PickableEntry("minecraft:fish", 2..32, 80),
            PickableEntry("minecraft:apple", 2..32, 55),
            PickableEntry("minecraft:book", 2..16, 80),
            PickableEntry("minecraft:cactus", 2..32, 80),
            PickableEntry("minecraft:diamond", 1..8, 2000),
            PickableEntry("minecraft:dispenser", 1..6, 200),
            PickableEntry("minecraft:iron_ingot", 1..32, 200)
    )

    val rewards = listOf(
            PickableEntry("minecraft:gold_nugget", 1..128, 100),
            PickableEntry("minecraft:gold_ingot", 1..128, 900)
    )

}