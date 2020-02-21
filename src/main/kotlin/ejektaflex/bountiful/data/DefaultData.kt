package ejektaflex.bountiful.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.BountyEntryEntity
import ejektaflex.bountiful.api.data.entry.BountyEntryStack
import ejektaflex.bountiful.api.data.json.JsonAdapter
import ejektaflex.bountiful.api.ext.ir
import ejektaflex.bountiful.api.generic.IIdentifiable
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import java.io.File

object DefaultData {

    private fun exportReg(reg: ValueRegistry<out IIdentifiable>, location: File) {
        for (item in reg) {
            File(location, "${item.id}.json").apply {
                createNewFile()

                val itemText = JsonAdapter.toJson(item, item::class)
                println("Serializing $item")
                println(itemText)
                writeText(itemText)
            }
        }
    }

    fun export() {
        BountifulMod.logger.info("Dumping default data into Bountiful data registries.")
        DecreeRegistry.restore(decrees.content)
        PoolRegistry.restore(pools.content)

        exportReg(decrees, BountifulMod.configDecrees)
        exportReg(pools, BountifulMod.configPools)

        println("Serial for this thingy: ${JsonAdapter.toJson(decrees.content.first())}")

        BountifulMod.logger.info("Data dumped, added default data")

    }

    val decrees = ValueRegistry<Decree>().apply {
        add(
                Decree(
                        "Village Rations: Food",
                        "The people are in need of food!",
                        "vanilla_food_rations",
                        spawnsInBoard = true,
                        objectivePools = mutableListOf(
                                "vanilla_food_bounties"
                        ),
                        rewardPools = mutableListOf(
                                "vanilla_food_rewards",
                                "vanilla_money_rewards"
                        )
                )
        )
    }



    val pools = ValueRegistry<EntryPool>().apply {
        add(
                EntryPool("vanilla_food_bounties").apply {
                    add(
                            BountyEntryStack().apply {
                                content = "minecraft:cod"
                                unitWorth = 80
                                amountRange = (2..32).ir
                            },
                            BountyEntryStack().apply {
                                content = "minecraft:coal"
                                unitWorth = 80
                                amountRange = (2..32).ir
                            },
                            /*
                            BountyEntryEntity().apply {
                                content = "minecraft:zombie"
                                unitWorth = 40
                                amountRange = (2..32).ir
                            },

                             */
                            BountyEntryStack().apply {
                                name = "Any Fish"
                                type = "tag"
                                content = "minecraft:fishes"
                                unitWorth = 150
                                amountRange = (4..24).ir
                            }
                            //BountyEntry("minecraft:bread", 60, amountRange = (1..12).ir),
                            //BountyEntry("minecraft:sugar", 55, amountRange = (1..64).ir),
                            //BountyEntry("minecraft:wheat", 20, amountRange = (1..48).ir)
                    )
                },

                EntryPool("vanilla_food_rewards").apply {
                    add(
                            //BountyEntryStack("minecraft:cake", 200, 1..3),
                            BountyEntryStack().apply {
                                content = "minecraft:baked_potato"
                                unitWorth = 120
                                amountRange = (4..24).ir
                            },
                            BountyEntryStack().apply {
                                content = "minecraft:cooked_mutton"
                                unitWorth = 150
                                amountRange = (4..24).ir
                            }
                            /*,
                            BountyEntryStack().apply {
                                content = "minecraft:cooked_beef"
                                unitWorth = 175
                                amountRange = (4..24).ir
                            },
                            BountyEntryStack().apply {
                                content = "minecraft:cooked_porkchop"
                                unitWorth = 60
                                amountRange = (1..24).ir
                            }

                             */
                    )
                },

                EntryPool("vanilla_money_rewards").apply {
                    add(
                            //BountyEntryStack("minecraft:gold_nugget", 100),
                            //BountyEntryStack("minecraft:gold_ingot", 900)
                            BountyEntryStack().apply {
                                content = "minecraft:gold_nugget"
                                unitWorth = 150
                                amountRange = (1..16).ir
                            }
                    )
                }

        )
    }
}