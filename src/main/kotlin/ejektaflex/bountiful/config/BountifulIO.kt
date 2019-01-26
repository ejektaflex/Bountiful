package ejektaflex.bountiful.config

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ejektaflex.bountiful.api.logic.picked.PickedEntry
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack
import ejektaflex.bountiful.registry.BountyRegistry
import ejektaflex.bountiful.registry.RewardRegistry
import ejektaflex.bountiful.registry.ValueRegistry
import java.io.File

object BountifulIO {

    fun ensureDirectory(base: File, name: String): File {
        val newDir = File(base, name)
        if (!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    fun populateConfigFolder(folder: File, defaultData: List<Any>, fileName: String): File {
        val gson = GsonBuilder().setPrettyPrinting().create()
        // Populate entries, fill if none exist
        val fileToPopulate = File(folder, fileName)
        if (!fileToPopulate.exists()) {
            fileToPopulate.apply {
                createNewFile()
                println("Going to serialize content..")
                val content = gson.toJson(defaultData.toTypedArray())
                //println("Content: $contentObj")
                writeText(content)
            }
        }
        return fileToPopulate
    }


    fun safeHotReloadAll(): List<String> {
        val retMsg = mutableListOf<String>()

        try {
            val oldBounties = BountyRegistry.backup()
            hotReloadBounties().also { invalids ->
                if (invalids.isNotEmpty()) {

                    invalids.forEach {
                        retMsg += "§4Invalid bounty: ${it.content}. Reverting to original data."
                    }

                }
                val numValidBounties = oldBounties.size - invalids.size
                if (numValidBounties < Bountiful.config.bountyAmountRange.last) {
                    retMsg += "§4Your config file ('bounties.json') does not contain enough valid bounties."
                    BountyRegistry.restore(oldBounties)
                }
            }
            retMsg += "Bounties Reloaded."
        } catch (e: Exception) {
            retMsg += "§4Invalid bounty json data. Details: "
            retMsg.addAll(e.message!!.split("\n").map { "§4$it" })
        }

        try {
            hotReloadRewards().forEach {
                retMsg += "§4Invalid reward: ${it.content}. Skipping."
            }
            retMsg += "Rewards Reloaded."
        } catch (e: Exception) {
            retMsg += "§4Invalid reward json data. Details: "
            retMsg.addAll(e.message!!.split("\n").map { "§4$it" })
        }

        return retMsg
    }

    private fun <T : COMMON, U : COMMON, COMMON : Any> hotReload(
            fileName: String,
            registry: ValueRegistry<U>,
            fileContentType: Class<Array<T>>,
            transform: (it: T) -> U,
            validity: (it: U) -> Boolean
    ): List<U> {
        // Comma after last element can actually insert a null element, so specify that the list
        // can have null elements and filter them out before replacing them
        val picked: List<T?> = try {
            Gson().fromJson(
                    File(Bountiful.configDir, fileName).readText(),
                    fileContentType
            ).toList()
        } catch (e: Exception) {
            throw Exception("JSON Structure of '$fileName' is incorrect! Reverting to previous data. Details: ${e.message}")
        }
        return registry.replace(picked.filterNotNull().map(transform), validity)
    }

    fun hotReloadBounties(): List<PickableEntry> {
        return hotReload("bounties.json", BountyRegistry, Array<PickableEntry>::class.java, { it -> it }) {
            it.isValid()
        }
    }

    fun hotReloadRewards(): List<PickedEntryStack> {
        return hotReload("rewards.json", RewardRegistry, Array<PickedEntry>::class.java, { PickedEntryStack(it) }) {
            it.isValid()
        }
    }


}