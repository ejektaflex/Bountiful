package bountiful.proxy

import bountiful.Bountiful
import bountiful.BountifulInfo
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


    private fun ensureDirectory(base: File, name: String): File {
        val newDir = File(base, name)
        if (!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    fun getPickables(fileName: String, str: String): List<PickableEntry> {
        val picked = Gson().fromJson(str, EntryPack::class.java) ?: throw Exception("File $fileName has invalid structure!")
        return picked.entries.toList()
    }


    fun populateConfigFolder(folder: File, defaultData: List<PickableEntry>, fileName: String): File {
        val gson = GsonBuilder().setPrettyPrinting().create()
        // Populate bounties, fill if none exist
        val fileToPopulate = File(folder, fileName)
        if (!fileToPopulate.exists()) {
            fileToPopulate.apply {
                createNewFile()
                val content = gson.toJson(EntryPack(defaultData.toTypedArray()))
                println("Content: $content")
                writeText(content)
            }
        }
        return fileToPopulate
    }


    override fun init(e: FMLInitializationEvent) {
        // Create folder
        val folder = ensureDirectory(Bountiful.configDir, BountifulInfo.MODID)

        // Populate bounties, fill if none exist
        val bountyList = populateConfigFolder(folder, DefaultData.bounties, "bounties.json")
        getPickables("bounties.json", bountyList.readText()).forEach {
            BountyRegistry.add(it)
        }

        // Same for rewards
        // Populate bounties, fill if none exist
        val rewardList = populateConfigFolder(folder, DefaultData.rewards, "rewards.json")
        getPickables("rewards.json", rewardList.readText()).forEach {
            RewardRegistry.add(it)
        }

        println("Bounties:")
        BountyRegistry.items.forEach { println(it) }

        println("Rewards:")
        RewardRegistry.items.forEach { println(it) }


    }

    override fun postInit(e: FMLPostInitializationEvent) {

    }
}