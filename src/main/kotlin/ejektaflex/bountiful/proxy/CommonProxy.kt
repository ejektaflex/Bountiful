package ejektaflex.bountiful.proxy

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.config.BountifulIO
import ejektaflex.bountiful.data.DefaultData
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent

open class CommonProxy : IProxy {

    override fun postInit(e: FMLPostInitializationEvent) {
        // Populate entries, fill if none exist
        "bounties.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.entries.items, it)
            val invalids = BountifulIO.hotReloadBounties(it)
            println("Invalid bounties: $invalids")
        }

        // Same for rewards
        "rewards.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.rewards.items.map { item ->
                item.genericPick
            }, it)
            val invalid = BountifulIO.hotReloadRewards(it)
            println("Invalid rewards: $invalid")
            //BountifulIO.hotReloadJson(RewardRegistry, it)
        }

        println("Bounties: ${BountyRegistry.items.size}")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards: ${RewardRegistry.items.size}")
        RewardRegistry.items.forEach { println(it) }
    }

}