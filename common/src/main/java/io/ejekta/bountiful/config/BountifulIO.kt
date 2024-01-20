package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.jvm.ensured
import io.ejekta.kambrikx.file.KambrikConfigFile
import io.ejekta.kambrikx.file.KambrikParseFailMode
import net.minecraft.resource.ResourceManager
import kotlin.math.min

object BountifulIO {

    private val rootFolder = Kambrik.File.getConfigFolderRelativePath(Bountiful.ID)

    private val configFile = KambrikConfigFile(
        rootFolder,
        "bountiful.json",
        JsonFormats.DataPack,
        KambrikParseFailMode.LEAVE,
        BountifulConfigData.serializer()
    ) { BountifulConfigData() }

    var configData = configFile.read()

    private val poolConfigs = rootFolder.ensured("bounty_pools")
    private val decreeConfigs = rootFolder.ensured("bounty_decrees")

    fun getPoolFile(poolName: String): KambrikConfigFile<Pool> {
        return KambrikConfigFile(
            poolConfigs,
            "$poolName.json", JsonFormats.Hand, KambrikParseFailMode.LEAVE, Pool.serializer()) {
            Pool().apply { setup(poolName) }
        }
    }

    private fun saveConfig() {
        configFile.write(configData)
    }

    fun loadConfig() {
        configData = configFile.read()
    }

    fun reloadConfig() {
        saveConfig()
        loadConfig()
    }

    fun doContentReload(manager: ResourceManager) {
        reloadConfig()
        contentLoaders.forEach {
            it.clearDestination()
            it.loadData(manager)
        }
        // After all data is loaded, lint it
        contentLoaders.forEach {
            it.lint()
        }
    }

    private val contentLoaders = listOf(
        ResourceLoadStrategy("Pool Loader", "bounty_pools", poolConfigs, Pool.serializer(), BountifulContent.Pools) {
            // If the pool isn't being used, that's usually problematic
            if (usedInDecrees.isEmpty()) {

                val poolAssoc = BountifulContent.Pools.filter { it.usedInDecrees.isNotEmpty() }.associateBy { it.id.toSet() }
                val poolQuery = id.toSet()

                // Compare to existing pool names based on char similarity to see if they just misspelled it
                val bestFit = poolAssoc.maxByOrNull { it.key.intersect(poolQuery).size }?.takeIf {
                    // Must be at most two characters off
                    it.key.intersect(poolQuery).size >= min(poolQuery.size, it.key.size) - 2
                }

                Bountiful.LOGGER.warn("Pool '$id' has been loaded, but is not attached to any existing data! This is probably a configuration error.")
                Bountiful.LOGGER.warn("* If you intended to add this data to an existing Pool, please use an existing Pool name instead of '$id'.")

                bestFit?.let {
                    Bountiful.LOGGER.warn("  * Did you mean to use the pool name '${it.value.id}' instead of '$id'?")
                }

                Bountiful.LOGGER.warn("* Otherwise, please add '$id' to a Decree.")
                Bountiful.LOGGER.warn("* NOTE: This data will not show up in game until one of the above fixes is made.")
            }
        },
        ResourceLoadStrategy("Decree Loader", "bounty_decrees", decreeConfigs, Decree.serializer(), BountifulContent.Decrees) {
            if (objectivePools.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has no Objective Pools! This is probably a configuration error.")
            } else if (objectives.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has one or more Objective Pools, but they are all empty!")
            }

            if (rewardPools.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has no Reward Pools! This is probably a configuration error.")
            } else if (rewards.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has one or more Reward Pools, but they are all empty!")
            }

            invalidPools.let {
                if (it.isNotEmpty()) {
                    Bountiful.LOGGER.warn("Decree '$id' references these pools, which do not exist: $it")
                }
            }

        }
    )

}
