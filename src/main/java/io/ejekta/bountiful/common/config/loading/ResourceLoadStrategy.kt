package io.ejekta.bountiful.common.config.loading

import io.ejekta.bountiful.common.config.IMerge
import io.ejekta.bountiful.common.serial.Format
import kotlinx.serialization.DeserializationStrategy
import net.minecraft.resource.ResourceManager
import net.minecraft.util.Identifier
import java.io.File
import java.nio.file.Path

class ResourceLoadStrategy<T : IMerge<T>>(
    private val strategyName: String,
    private val folderName: String,
    private val configPath: Path,
    private val decoder: DeserializationStrategy<T>,
    private val destination: MutableList<T>
) {

    private fun decode(fileText: String, newId: String): T? {
        return try {
            Format.Normal.decodeFromString(decoder, fileText).apply {
                id = newId
            }
        } catch (e: Exception) {
            println("Could not decode file with ${this::class.simpleName}, given id $newId in folder $folderName")
            e.printStackTrace()
            null
        }
    }

    private fun getResources(manager: ResourceManager): List<Identifier> {
        return manager.findResources(folderName) {
            it.endsWith(".json")
        }.map {
            it
        }
    }

    private fun loadFile(file: File): T? {
        val fileContent = file.readText()
        return decode(fileContent, file.nameWithoutExtension)
    }

    private fun loadResource(id: Identifier, manager: ResourceManager): T? {
        val resourceContent = manager.read(id)
        return decode(resourceContent, id.fileName())
    }

    fun clearDestination() {
        destination.clear()
    }

    fun loadResources(manager: ResourceManager) {
        getResources(manager).groupBy {
            it.fileName()
        }.forEach { (itemId, resources) ->
            println("Loading $strategyName: $itemId")

            val items = resources.mapNotNull {
                loadResource(it, manager)
            }.filter {
                it.canLoad
            }.takeIf {
                it.isNotEmpty()
            }

            items?.reduce {
                    a, b -> a.merged(b)
            }?.also {
                destination.add(it)
            }
        }
    }

    // Loads config files from ./config/bountiful/${folderName}
    fun loadFiles() {
        configPath.toFile().listFiles()?.forEach { file ->
            println("Found decree config file: $file")
            val item = loadFile(file)
            if (item != null) {
                val existing = destination.find { it.id == item.id }
                existing?.let {
                    println("Merging in config from ${file.path}..")
                    it.merge(item)
                }
            }
        }
    }

    companion object {
        private fun ResourceManager.read(id: Identifier): String {
            return getResource(id).inputStream.reader().readText()
        }

        private fun Identifier.fileName(): String {
            return path.substringAfterLast("/").substringBefore(".")
        }
    }

}