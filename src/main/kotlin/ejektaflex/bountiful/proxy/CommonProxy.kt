package ejektaflex.bountiful.proxy

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

open class CommonProxy : IProxy {








    override fun init(e: FMLInitializationEvent) {

        // Populate entries, fill if none exist
        "bounties.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.entries.items, it)
            //BountifulIO.hotReloadJson(BountyRegistry, it)
        }

        // Same for rewards
        "rewards.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.rewards.items.map { item ->
                item.genericPick
            }, it)
            //BountifulIO.hotReloadJson(RewardRegistry, it)
        }


        println("Bounties:")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards:")
        RewardRegistry.items.forEach { println(it) }


    }

    override fun postInit(e: FMLPostInitializationEvent) {

    }
}