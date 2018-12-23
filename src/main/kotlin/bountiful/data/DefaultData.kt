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
            PickableEntry("minecraft:iron_ingot", 1..32, 200),
            PickableEntry("minecraft:bread", 1..12, 60),
            PickableEntry("minecraft:rotten_flesh", 1..24, 35),
            PickableEntry("minecraft:sign", 1..16, 65),
            PickableEntry("minecraft:slime_ball", 1..32, 60),
            PickableEntry("minecraft:spider_eye", 1..16, 45),
            PickableEntry("minecraft:string", 1..24, 35),
            PickableEntry("minecraft:sugar", 1..64, 30),
            PickableEntry("minecraft:tripwire_hook", 1..8, 60),
            PickableEntry("minecraft:wheat", 1..48, 20)
    )

    val rewards = listOf(
            PickableEntry("minecraft:gold_nugget", 1..128, 100),
            PickableEntry("minecraft:gold_ingot", 1..128, 900)
    )

}