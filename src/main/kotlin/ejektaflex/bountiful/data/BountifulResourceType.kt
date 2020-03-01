package ejektaflex.bountiful.data

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraftforge.resource.IResourceType
import java.io.File
import kotlin.reflect.KClass

enum class BountifulResourceType(val folderLoc: File, val reg: ValueRegistry<out Any>, val klazz: KClass<out Any>) : IResourceType {

    DECREES(BountifulMod.configDecrees, DecreeRegistry, Decree::class),
    POOLS(BountifulMod.configPools, PoolRegistry, EntryPool::class);

    val folderName: String
        get() = folderLoc.nameWithoutExtension

}