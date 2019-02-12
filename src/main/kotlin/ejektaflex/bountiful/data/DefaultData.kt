package ejektaflex.bountiful.data

import ejektaflex.bountiful.api.logic.ItemRange
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
import ejektaflex.bountiful.registry.ValueRegistry

object DefaultData {

    val entries = ValueRegistry<PickableEntry>().apply {
        add(
            PickableEntry("minecraft:dirt", 16..128, 5),
            PickableEntry("minecraft:stone", 16..128, 10),
            PickableEntry("minecraft:cobblestone", 16..128, 7),
            PickableEntry("minecraft:fish", 2..32, 80),
            PickableEntry("minecraft:apple", 2..32, 70),
            PickableEntry("minecraft:book", 2..16, 80),
            PickableEntry("minecraft:cactus", 2..32, 80),
            PickableEntry("minecraft:diamond", 1..8, 1600),
            PickableEntry("minecraft:dispenser", 1..6, 200),
            PickableEntry("minecraft:iron_ingot", 1..32, 200),
            PickableEntry("minecraft:bread", 1..12, 60),
            PickableEntry("minecraft:rotten_flesh", 1..24, 55),
            PickableEntry("minecraft:sign", 1..16, 65),
            PickableEntry("minecraft:slime_ball", 1..32, 60),
            PickableEntry("minecraft:spider_eye", 1..16, 75),
            PickableEntry("minecraft:string", 1..24, 35),
            PickableEntry("minecraft:sugar", 1..64, 55),
            PickableEntry("minecraft:tripwire_hook", 1..8, 70),
            PickableEntry("minecraft:wheat", 1..48, 20),
            PickableEntry("minecraft:leather", 1..12, 90),
            PickableEntry("entity:minecraft:zombie", 1..8, 120, weight = 300),
            PickableEntry("entity:minecraft:skeleton", 1..6, 140, weight = 250),
            PickableEntry("minecraft:potion", 1..3, 300, nbtJson = "{Potion: \"minecraft:healing\"}")
        )
    }

    val rewards = ValueRegistry<PickedEntryStack>().apply {
        add(
                PickedEntryStack(PickedEntry("minecraft:gold_nugget", 100)),
                PickedEntryStack(PickedEntry("minecraft:gold_ingot", 900)),
                PickedEntryStack(PickedEntry("minecraft:diamond", 2400, 10)),
                //PickedEntryStack(PickedEntry("minecraft:potion", 800, 5)),
                PickedEntryStack(PickedEntry("minecraft:iron_sword", 750, 5, nbtJson = "{display:{Lore:[\"Sharper than Usual.\"]},ench:[{id:16,lvl:1}]}", range = ItemRange(1, 1))),
                PickedEntryStack(PickedEntry("minecraft:leather_helmet", 500, 5, range = ItemRange(1, 1))),
                PickedEntryStack(PickedEntry("minecraft:leather_boots", 450, 5, range = ItemRange(1, 1))),
                PickedEntryStack(PickedEntry("minecraft:iron_chestplate", 1800, 5, range = ItemRange(1, 1))),
                PickedEntryStack(PickedEntry("minecraft:golden_chestplate", 1200, 5, range = ItemRange(1, 1))),
                PickedEntryStack(PickedEntry("minecraft:bow", 1300, 10, "{ench:[{id:48,lvl:2}]}", range = ItemRange(1, 1)))
        )
    }

}