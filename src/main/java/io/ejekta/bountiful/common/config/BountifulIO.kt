package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.data.pool.Decree
import io.ejekta.bountiful.common.bounty.data.pool.Pool
import io.ejekta.bountiful.common.content.BountifulContent
import io.ejekta.bountiful.common.serial.Format
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

object BountifulIO : SimpleSynchronousResourceReloadListener {

    private enum class ResourceType(val folderName: String) {
        POOL("bounty_pools"),
        DECREE("bounty_decrees");

        fun forFolder(name: String) = values().first { it.folderName == name }
    }

    val Path.assuredly: Path
        get() = also {
            it.toFile().apply {
                mkdirs()
            }
        }

    private val configFolder: Path by lazy {
        Paths.get("config/bountiful").assuredly
    }

    private val poolConfigs: Path by lazy {
        configFolder.resolve(ResourceType.POOL.folderName).assuredly
    }

    private val decreeConfigs: Path by lazy {
        configFolder.resolve(ResourceType.DECREE.folderName).assuredly
    }

    private data class BountifulResource(
        val id: Identifier,
        val type: ResourceType
    ) {
        val base = id.namespace
        val target = id.path.substringAfter("${type.folderName}/").substringBefore("/")
        val name = id.path.substringAfterLast("/").substringBefore(".json")
    }

    private fun loadPool(res: BountifulResource, manager: ResourceManager): Pool {
        println("Trying to load pool piece: ${res.id}")
        if (res.type != ResourceType.POOL) throw Exception("$res is not a Pool!")
        val content = manager.getResource(res.id).inputStream.reader().readText()
        return Format.Normal.decodeFromString(Pool.serializer(), content).apply {
            id = res.name
        }
    }

    private fun loadPool(file: File): Pool {
        val content = file.readText()
        return Format.Normal.decodeFromString(Pool.serializer(), content).apply {
            id = file.nameWithoutExtension
        }
    }

    private fun loadDecree(file: File): Decree {
        val content = file.readText()
        return Format.Normal.decodeFromString(Decree.serializer(), content).apply {
            id = file.nameWithoutExtension
        }
    }

    private fun loadDecree(res: BountifulResource, manager: ResourceManager): Decree {
        if (res.type != ResourceType.DECREE) throw Exception("$res is not a Decree!")
        val content = manager.getResource(res.id).inputStream.reader().readText()
        return Format.Normal.decodeFromString(Decree.serializer(), content).apply {
            id = res.name
        }
    }

    private fun getResources(manager: ResourceManager, type: ResourceType): List<BountifulResource> {
        return manager.findResources(type.folderName) {
            it.endsWith(".json")
        }.map {
            if (it.path.count { char -> char == '/' } > 2) {
                throw Exception("Bountiful resource may be in the wrong place!: $it")
            }

            BountifulResource(it, type)
        }
    }

    override fun apply(resourceManager: ResourceManager) {

        BountifulContent.Pools.clear()
        BountifulContent.Decrees.clear()

        getResources(resourceManager, ResourceType.POOL).groupBy {
            it.name
        }.forEach { (poolId, resources) ->
            println("Loading Pool: $poolId")
            val pools = resources.map { loadPool(it, resourceManager) }
            val pool = pools.reduce { a, b -> a.merged(b) }
            BountifulContent.Pools.add(pool)
        }

        getResources(resourceManager, ResourceType.DECREE).groupBy {
            it.name
        }.forEach { (poolId, resources) ->
            println("Loading Decree: $poolId")
            val decrees = resources.map { loadDecree(it, resourceManager) }
            val decree = decrees.reduce { a, b -> a.merged(b) }
            BountifulContent.Decrees.add(decree)
        }

        println("Printing all pool config files: ")
        poolConfigs.toFile().listFiles()?.forEach { file ->
            println("Found pool config file: $file")
            val pool = loadPool(file)
            val existingPool = BountifulContent.Pools.find { it.id == pool.id }
            existingPool?.let {
                println("Merging in config pool..")
                it.merge(pool)
            }
        }

        println("Printing all decree config files: ")
        decreeConfigs.toFile().listFiles()?.forEach { file ->
            println("Found decree config file: $file")
            val pool = loadDecree(file)
            val existingPool = BountifulContent.Decrees.find { it.id == pool.id }
            existingPool?.let {
                println("Merging in config decree..")
                it.merge(pool)
            }
        }

    }

    override fun getFabricId() = Bountiful.id("reload_listener")
}
