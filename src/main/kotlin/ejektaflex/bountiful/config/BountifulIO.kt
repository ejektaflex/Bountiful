package ejektaflex.bountiful.config

import ejektaflex.bountiful.Bountiful
import ejektaflex.bountiful.data.SaveWrapper
import ejektaflex.bountiful.api.logic.pickable.PickableEntry
import ejektaflex.bountiful.registry.ValueRegistry
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

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
        val picked = Gson().fromJson(str, SaveWrapper::class.java) ?: throw Exception("File $fileName has invalid structure!")
        return picked.entries.toList().mapNotNull { it as? T }
    }

    fun populateConfigFolder(folder: File, defaultData: List<Any>, fileName: String): File {
        val gson = GsonBuilder().setPrettyPrinting().create()
        // Populate entries, fill if none exist
        val fileToPopulate = File(folder, fileName)
        if (!fileToPopulate.exists()) {
            fileToPopulate.apply {
                createNewFile()
                println("Going to serialize content..")
                val content = gson.toJson(SaveWrapper(ArrayList(defaultData)))
                println("Content: $content")
                writeText(content)
            }
        }
        return fileToPopulate
    }

    /*
    fun <T : Any> hotReloadJson(registry: ValueRegistry<T>, fileName: String) {
        //val backup = registry.backup()

        registry.empty()
        val picked = Gson().fromJson(
                File(Bountiful.configDir, fileName).readText(),
                SaveWrapper(ArrayList())::class.java
        ) ?: throw Exception("File $fileName has invalid structure!")

        picked.entries.forEach {
            registry.add(it)
        }
    }
    */

    fun <T : Any> typeOf(): Type {
        return object : TypeToken<T>() {}.type
    }


}