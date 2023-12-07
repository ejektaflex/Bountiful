package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.ext.jvm.ensured
import io.ejekta.kambrikx.file.KambrikConfigFile
import io.ejekta.kambrikx.file.KambrikParseFailMode
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.util.*

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

    fun saveConfig() {
        configFile.write(configData)
    }

    fun loadConfig() {
        configData = configFile.read()
    }

    fun doContentReload(manager: ResourceManager) {
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
            if (usedInDecrees.isEmpty()) {
                Bountiful.LOGGER.warn("Pool '$id' is not used in any Decrees! This is probably a configuration error. Did you name this file correctly?")
                Bountiful.LOGGER.warn("Please add '$id' to a Decree, or it will not have any effect.")
            }
        },
        ResourceLoadStrategy("Decree Loader", "bounty_decrees", decreeConfigs, Decree.serializer(), BountifulContent.Decrees) {
            if (objectivePools.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has no Objective Pools!")
            } else if (objectives.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has one or more Objective Pools, but they are all empty!")
            }

            if (rewardPools.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has no Reward Pools!")
            } else if (rewards.isEmpty()) {
                Bountiful.LOGGER.warn("Decree '$id' has one or more Reward Pools, but they are all empty!")
            }
        }
    )

}
