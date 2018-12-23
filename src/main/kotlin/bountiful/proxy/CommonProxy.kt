package bountiful.proxy

import bountiful.Bountiful
import bountiful.BountifulInfo
import bountiful.data.DefaultData
import bountiful.data.EntryPack
import bountiful.ext.rl
import bountiful.ext.toStringContents
import bountiful.registry.BountyRegistry
import bountiful.logic.PickableEntry
import bountiful.registry.RewardRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File
import java.lang.Exception

open class CommonProxy : IProxy {


    private fun ensureDirectory(base: File, name: String): File {
        val newDir = File(base, name)
        if (!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    fun getPickables(fileName: String, str: String): List<PickableEntry> {
        val picked = Gson().fromJson(str, EntryPack::class.java)
        if (picked == null) {
            throw Exception("File $fileName has invalid structure!")
        }
        return picked!!.entries.toList()
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
        /*
        BountyRegistry.add(
                "minecraft:dirt".toItemStack!! to 20,
                "minecraft:iron_ore".toItemStack!! to 3000,
                "minecraft:gold_ore".toItemStack!! to 9000,
                "minecraft:iron_ingot".toItemStack!! to 9000,
                "minecraft:ender_pearl".toItemStack!! to 32_000,
                "minecraft:diamond".toItemStack!! to 64_000,
                "minecraft:cauldron".toItemStack!! to 72_000,
                "minecraft:minecart".toItemStack!! to 54_000
        )

        RewardRegistry.add(
                "minecraft:iron_nugget".toItemStack!! to 1000,
                "minecraft:iron_ingot".toItemStack!! to 9000
        )
        */
    }
}