package ejektaflex.bountiful.config

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.data.EntryPack
import ejektaflex.bountiful.logic.error.BountyCreationException
import ejektaflex.bountiful.logic.pickable.PickableEntry
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

    fun getPickables(fileName: String, str: String): List<PickableEntry> {
        val picked = Gson().fromJson(str, EntryPack::class.java) ?: throw Exception("File $fileName has invalid structure!")
        return picked.entries.toList()
    }

    fun populateConfigFolder(folder: File, defaultData: List<PickableEntry>, fileName: String): File {
        val gson = GsonBuilder().setPrettyPrinting().create()
        // Populate bounties, fill if none exist
        val fileToPopulate = File(folder, fileName)
        if (!fileToPopulate.exists()) {
            fileToPopulate.apply {
                createNewFile()
                val content = gson.toJson(EntryPack(defaultData.toTypedArray()))
                println("Content: $content")
                writeText(content)
            }
        }
        return fileToPopulate
    }

    fun hotReloadJson(registry: ValueRegistry, fileName: String) {
        val backup = registry.backup()
        registry.empty()
        getPickables(fileName, File(Bountiful.configDir, fileName).readText()).forEach {
            if (it.itemStack == null) {
                registry.restore(backup)
                throw BountyCreationException("Could not create a bounty from: '${it.itemString}', it might be misspelled?")
            } else {
                registry.add(it)
            }
        }
    }

}