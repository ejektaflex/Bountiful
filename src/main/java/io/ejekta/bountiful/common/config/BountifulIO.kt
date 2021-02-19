package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.config.loading.ResourceLoadStrategy
import io.ejekta.bountiful.common.content.BountifulContent
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

    private val configFolder: Path by lazy { Paths.get("config/bountiful").assuredly }
    private val poolConfigs: Path by lazy { configFolder.resolve("bounty_pools").assuredly }
    private val decreeConfigs: Path by lazy { configFolder.resolve("bounty_decrees").assuredly }
    private val configFile: File by lazy { configFolder.resolve("bountiful.json").toFile() }

    private fun poolConfigFiles(): List<File> {
        return poolConfigs.toFile().listFiles { dir, name -> dir.isFile && name.endsWith(".json") }?.toList() ?: listOf()
    }

    fun getOrCreatePoolConfig(name: String): File {
        return poolConfigFiles().find { it.nameWithoutExtension == name } ?: File(poolConfigs.toFile(), "${name}.json").apply {
            createNewFile()
            val emptyPool = Pool().apply { setup(name) }
            writeText(
                Format.DataPack.encodeToString(Pool.serializer(), emptyPool)
            )
        }
    }

    fun editPoolConfig(name: String, func: Pool.() -> Unit) {
        val file = getOrCreatePoolConfig(name)
        val text = file.readText()
        val pool = Format.DataPack.decodeFromString(Pool.serializer(), text)
        pool.func()
    }

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
