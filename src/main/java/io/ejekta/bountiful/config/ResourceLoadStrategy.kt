package io.ejekta.bountiful.config

import io.ejekta.bountiful.data.IMerge
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

    private fun decode(identifier: Identifier, fileText: String, newId: String): T? {
        return try {
            JsonFormats.Hand.decodeFromString(decoder, fileText).apply {
                id = newId
            }
        } catch (e: Exception) {
            println("Could not decode file with ${this::class.simpleName}, given id '$newId' in folder '$folderName' on id $identifier")
            e.printStackTrace()
            null
        }
    }

    private fun getConfigFile(id: Identifier): File {
        val fileName = id.fileName() + ".json"
        return File(configPath.toFile(), fileName)
    }

    fun loadData(manager: ResourceManager) {
        val resourceMap = getResources(manager).groupBy {
            it.fileName()
        }

        for ((itemId, resources) in resourceMap) {
            println("Querying $strategyName: $itemId, $resources")

            val referenceId = resources.first()
            val matchingFile = getConfigFile(referenceId)

            var configData: T? = null

            // Dig through config folder to see if we have a config first
            if (matchingFile.exists()) {
                configData = loadFile(referenceId)

                if (configData != null) {
                    // If config data replaces resource data, don't even load resource data
                    if (configData.replace) {
                        destination.add(configData)
                        println("Config REPLACES so we are done here")
                        continue
                    }
                }

            }

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
                // Merge with config if possible. Else just add resource data
                if (configData != null) {
                    val mergedWithConfig = it.merged(configData)
                    destination.add(mergedWithConfig)
                } else {
                    destination.add(it)
                }
            }

        }
    }

    private fun getResources(manager: ResourceManager): List<Identifier> {
        return manager.findResources(folderName) {
            it.endsWith(".json")
        }.toList()
    }

    private fun loadFile(id: Identifier): T? {
        val file = getConfigFile(id)
        val fileContent = file.readText()
        return decode(id, fileContent, file.nameWithoutExtension)
    }

    private fun loadResource(id: Identifier, manager: ResourceManager): T? {
        val resourceContent = manager.read(id)
        return decode(id, resourceContent, id.fileName())
    }

    fun clearDestination() {
        destination.clear()
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