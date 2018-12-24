package bountiful.proxy

import bountiful.Bountiful
import bountiful.BountifulInfo
import bountiful.config.BountifulIO
import bountiful.data.DefaultData
import bountiful.data.EntryPack
import bountiful.logic.pickable.PickableEntry
import bountiful.registry.BountyRegistry
import bountiful.registry.RewardRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import java.io.File

open class CommonProxy : IProxy {








    override fun init(e: FMLInitializationEvent) {

        // Populate bounties, fill if none exist
        "bounties.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.bounties, it)
            BountifulIO.hotReloadJson(BountyRegistry, it)
        }

        // Same for rewards
        "rewards.json".let {
            BountifulIO.populateConfigFolder(Bountiful.configDir, DefaultData.rewards, it)
            BountifulIO.hotReloadJson(RewardRegistry, it)
        }


        println("Bounties:")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards:")
        RewardRegistry.items.forEach { println(it) }


    }

    override fun postInit(e: FMLPostInitializationEvent) {

    }
}