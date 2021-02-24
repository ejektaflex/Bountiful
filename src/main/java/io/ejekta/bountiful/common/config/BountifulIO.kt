package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.data.Decree
import io.ejekta.bountiful.common.data.Pool
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.api.file.KambrikConfigFile
import io.ejekta.kambrik.api.file.KambrikConfigLocation
import io.ejekta.kambrik.api.file.KambrikReadFailMode
import io.ejekta.kambrik.ext.ensured
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import java.io.File
import java.nio.file.Paths

object BountifulIO : SimpleSynchronousResourceReloadListener {

    var configData = BountifulConfigData()

    private val rootFolder = Kambrik.File.getBaseFolder(Bountiful.ID)

    private val poolConfigs = rootFolder.ensured("bounty_pools")
    private val decreeConfigs = rootFolder.ensured("bounty_decrees")

    private val configFile = KambrikConfigFile(rootFolder, "bountiful.json", Format.DataPack, KambrikReadFailMode.LEAVE, BountifulConfigData.serializer()) { BountifulConfigData() }

    fun getPoolFile(poolName: String): KambrikConfigFile<Pool> {
        return KambrikConfigFile(
            poolConfigs,
            "$poolName.json", Format.Hand, KambrikReadFailMode.LEAVE, Pool.serializer()) {
            Pool().apply { setup(poolName) }
        }
    }

    fun saveConfig() {
        configFile.write(configData)
    }

    fun loadConfig() {
        configData = configFile.read()
    }

    private val contentLoaders = listOf(
        ResourceLoadStrategy("Pool Loader", "bounty_pools", poolConfigs, Pool.serializer(), BountifulContent.Pools),
        ResourceLoadStrategy("Decree Loader", "bounty_decrees", decreeConfigs, Decree.serializer(), BountifulContent.Decrees)
    )

    override fun apply(resourceManager: ResourceManager) {
        contentLoaders.forEach {
            it.clearDestination()
            it.loadResources(resourceManager)
            it.loadFiles()
        }
    }

    override fun getFabricId() = Bountiful.id("reload_listener")
}
