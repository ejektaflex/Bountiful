package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.config.loading.ResourceLoadStrategy
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.serial.Format
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

object BountifulIO : SimpleSynchronousResourceReloadListener {

    var config = BountifulConfig()

    private val Path.assuredly: Path
        get() = also {
            it.toFile().apply {
                mkdirs()
            }
        }

    private val configFolder: Path by lazy {
        Paths.get("config/bountiful").assuredly
    }

    private val poolConfigs: Path by lazy { configFolder.resolve("bounty_pools").assuredly }

    private val decreeConfigs: Path by lazy { configFolder.resolve("bounty_decrees").assuredly }

    private val configFile: File by lazy { configFolder.resolve("bountiful.json").toFile() }

    fun saveConfig() {
        configFile.writeText(
            Format.DataPack.encodeToString(BountifulConfig.serializer(), config)
        )
    }

    fun loadConfig() {
        config = try {
            val content = if (configFile.exists()) configFile.readText() else "{}"
            Format.DataPack.decodeFromString(BountifulConfig.serializer(), content)
        } catch (e: Exception) {
            println("Bountiful could not load it's config file. Using default.. ")
            e.printStackTrace()
            BountifulConfig()
        }

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
