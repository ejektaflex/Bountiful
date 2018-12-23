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
        // Create folder
        val folder = BountifulIO.ensureDirectory(Bountiful.configDir, BountifulInfo.MODID)

        // Populate bounties, fill if none exist
        BountifulIO.populateConfigFolder(folder, DefaultData.bounties, "bounties.json")
        BountifulIO.hotReloadJson(BountyRegistry, "bounties.json")

        // Same for rewards
        BountifulIO.populateConfigFolder(folder, DefaultData.rewards, "rewards.json")
        BountifulIO.hotReloadJson(RewardRegistry, "rewards.json")

        println("Bounties:")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards:")
        RewardRegistry.items.forEach { println(it) }


    }

    override fun postInit(e: FMLPostInitializationEvent) {

    }
}