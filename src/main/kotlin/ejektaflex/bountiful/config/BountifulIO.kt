package ejektaflex.bountiful.config

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.data.EntryPack
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.registry.ValueRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object BountifulIO {

    fun ensureDirectory(base: File, name: String): File {
        val newDir = File(base, name)
        if (!newDir.exists()) {
            newDir.mkdirs()
        }
        return newDir
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : PickableEntry> getPickables(fileName: String, str: String): List<PickableEntry> {
        val picked = Gson().fromJson(str, EntryPack::class.java) ?: throw Exception("File $fileName has invalid structure!")
        return picked.entries.toList().mapNotNull { it as? T }
    }

    fun populateConfigFolder(folder: File, defaultData: List<*>, fileName: String): File {
        val gson = GsonBuilder().setPrettyPrinting().create()
        // Populate entries, fill if none exist
        val fileToPopulate = File(folder, fileName)
        if (!fileToPopulate.exists()) {
            fileToPopulate.apply {
                createNewFile()
                println("Going to serialize content..")
                val content = gson.toJson(defaultData)
                println("Content: $content")
                writeText(content)
            }
        }
        return fileToPopulate
    }

    /*
    fun <T : Any> hotReloadJson(registry: ValueRegistry<T>, fileName: String) {
        val backup = registry.backup()
        registry.empty()
        getPickables<T>(fileName, File(Bountiful.configDir, fileName).readText()).forEach {
            if (it.genericPick().typed().content == null) {
                registry.restore(backup)
                throw BountyCreationException("Could not create a bounty from: '${it.content}', it might be misspelled?")
            } else {
                registry.add(it)
            }
        }
    }
    */

}