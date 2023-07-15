import java.io.File
import java.nio.file.Paths
import java.util.*

val packIcon = File("common/src/main/resources/assets/bountiful/textures/block/bountyboard.png")
val fabricGenPacks = File("common/src/main/resources/resourcepacks")
val dataGenFolder = File("datagen/data/content")
val replacements = File(dataGenFolder, "names.txt").readLines().map {
    it.split("|")
}.associate {
    it[0] to it[1]
}

fun createResourcePacks() {
    dataGenFolder.folderIter { platFolder ->
        println("Platform Folder Name: $platFolder")

        platFolder.folderIter { folder ->
            println("* Creating pack for ${folder.name}")
            when (platFolder.name) {
                "common" -> {
                    if (folder.name != "bountiful") {
                        createFabricPack(folder)
                    }
                    if (folder.name == "bountiful") {
                        createMainPack(folder)
                    } else {
                        createForgePack(folder)
                    }
                }
                "fabric" -> {
                    if (folder.name != "bountiful") {
                        createFabricPack(folder)
                    }
                }

            }
        }


    }
}

fun createFabricPack(from: File) {
    val dest = File("fabric/src/main/resources/resourcepacks")
    val fromName = from.name

    val root = File(dest, "compat-$fromName").apply {
        if (exists()) {
            deleteRecursively()
        }
        mkdirs()
    }

    // Emit pack file and logo file

    val packFile = File(root, "pack.mcmeta").apply {
        val newName = replacements[fromName] ?: fromName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        writeText("""
        {
            "pack": {
                "pack_format": 15,
                "description": "Adds $newName Compatibility to Bountiful"
            }
        }
        """.trimIndent())
    }

    val logoFile = File(root, "pack.png")
    packIcon.copyTo(logoFile)

    val dataFolder = File(root, "data/bountiful")

    createBuiltInPack(dataFolder, from)
}

fun createBuiltInPack(dest: File, from: File) {
    from.folderIter { subFolder ->
        val finalPath = Paths.get(dest.path, subFolder.name, from.name).toFile().apply {
            if (exists()) {
                deleteRecursively()
            }
        }
        subFolder.copyRecursively(finalPath)
    }
}

fun createForgePack(from: File) {
    val dest = File("forge/src/main/resources/data/bountiful")
    createBuiltInPack(dest, from)
}

fun createMainPack(from: File) {
    val dest = File("common/src/main/resources/data/bountiful")
    createBuiltInPack(dest, from)
}

fun main() {
    createResourcePacks()
    println("Resource pack gen done.")
}

