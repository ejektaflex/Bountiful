package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.IMerge
import io.ejekta.bountiful.data.Pool
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
    private val destination: MutableList<T>,
    private val postLintFunc: T.() -> Unit
) {

    private fun decode(identifier: Identifier, fileText: String, newId: String): T? {
        return try {
            JsonFormats.DataPack.decodeFromString(decoder, fileText).apply {
                id = newId

                // Set up pool, if it's a pool
                if (this is Pool) {
                    setup(newId)
                }
            }
        } catch (e: Exception) {
            Bountiful.LOGGER.error("Could not decode file with ${this::class.simpleName}, given id '$newId' in folder '$folderName' on id $identifier")
            e.printStackTrace()
            null
        }
    }

    private val loadedLocations = mutableSetOf<String>()

    private fun getConfigFile(id: Identifier): File {
        val fileName = id.fileName() + ".json"
        val default = File(configPath.toFile(), fileName)

        val isValid: File.() -> Boolean = {
            exists() && isFile && name == fileName
        }

        // Look for config files anywhere in the config path that matches this name
        val found = configPath.toFile().walk().filter(isValid).toList()

        // Return found file, or base file if none is found
        val toUse = found.firstOrNull() ?: default

        // Make sure the user doesn't have more than one file that matches the ID, otherwise tell them
        if (found.size > 1) {
            Bountiful.LOGGER.error("More than one config file in '$configPath' has the name name! This will result in unexpected behaviour!")
            for (f in found) {
                Bountiful.LOGGER.error("* ${f.path}")
            }
            Bountiful.LOGGER.error("Using this one, since we found it first:")
            Bountiful.LOGGER.error("* ${toUse.path}")
        }
        return toUse
    }

    private fun completeLoadOf(data: T) {
        destination.add(data)
        loadedLocations += data.id
    }

    fun loadData(manager: ResourceManager) {
        val resourceMap = getResources(manager).groupBy {
            it.fileName()
        }

        for ((itemId, resources) in resourceMap) {
            Bountiful.LOGGER.debug("Querying $strategyName: $itemId, $resources")

            val referenceId = resources.first()
            val matchingFile = getConfigFile(referenceId)

            var configData: T? = null

            // Dig through config folder to see if we have a config first
            if (matchingFile.exists()) {
                configData = loadFile(referenceId)

                if (configData != null) {
                    // If config data replaces resource data, don't even load resource data
                    if (configData.replace) {
                        completeLoadOf(configData)
                        Bountiful.LOGGER.debug("Config REPLACES so we are done here")
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
                it.finishMergedSetup()
                // Merge with config if possible. Else just add resource data
                if (configData != null) {
                    val mergedWithConfig = it.merged(configData)
                    completeLoadOf(mergedWithConfig)
                } else {
                    completeLoadOf(it)
                }
            }

        }
        loadUnloadedFiles()
    }

    fun lint() {
        for (item in destination) {
            item.postLintFunc()
        }
    }

    private fun getResources(manager: ResourceManager): List<Identifier> {
        return manager.findResources(folderName) {
            it.toString().endsWith(".json")
        }.filter {
            val str = it.key.path.substringBefore(".json")
            val idreg = "([A-Za-z_/]+)"
            val regexes = !BountifulIO.configData.general.dataPackExclusions.map { exc ->
                Regex(
                    exc.replace(Regex("[*]"), idreg)
                )
            }.any { regex ->
                regex.matches(str)
            }

            regexes
        }.keys.toList()
    }

    private fun loadFile(id: Identifier): T? {
        val file = getConfigFile(id)
        if (file.exists()) {
            Bountiful.LOGGER.info("Reading config file: ${file.absolutePath}")
            val fileContent = file.readText()
            return decode(id, fileContent, file.nameWithoutExtension)
        }
        return null
    }

    private fun loadResource(id: Identifier, manager: ResourceManager): T? {
        val resourceContent = manager.read(id)
        return decode(id, resourceContent, id.fileName())
    }

    private fun loadUnloadedFiles() {
        Bountiful.LOGGER.info("Trying to load unloaded files from: $configPath")
        configPath.toFile().listFiles()?.forEach { file ->
            if (file.nameWithoutExtension !in loadedLocations && file.extension == "json") {
                val resourceName = configPath.toString().replace('\\', '/') + "/" + file.nameWithoutExtension
                val fileId = Bountiful.id(resourceName)
                val item = loadFile(fileId)
                item?.let {
                    it.finishMergedSetup() // Normalize and such
                    Bountiful.LOGGER.debug("Completing load of $it from $fileId")
                    completeLoadOf(it)
                }
            }
        }
    }

    fun clearDestination() {
        loadedLocations.clear()
        destination.clear()
    }

    companion object {
        private fun ResourceManager.read(id: Identifier): String {
            return getResource(id).get().inputStream.reader().readText()
        }

        private fun Identifier.fileName(): String {
            return path.substringAfterLast("/").substringBefore(".")
        }
    }

}