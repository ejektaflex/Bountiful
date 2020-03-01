package ejektaflex.bountiful.data

import ejektaflex.bountiful.BountifulMod
import net.minecraftforge.resource.IResourceType
import java.io.File

enum class BountifulResourceType(val folderLoc: File) : IResourceType {

    DECREES(BountifulMod.configDecrees),
    POOLS(BountifulMod.configPools);

    val folderName: String
        get() = folderLoc.nameWithoutExtension

}