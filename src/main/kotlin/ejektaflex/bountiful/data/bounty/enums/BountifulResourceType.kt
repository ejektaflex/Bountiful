package ejektaflex.bountiful.data.bounty.enums

import ejektaflex.bountiful.data.structure.Decree
import ejektaflex.bountiful.data.structure.EntryPool
import ejektaflex.bountiful.generic.ValueRegistry
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraftforge.resource.IResourceType
import kotlin.reflect.KClass

enum class BountifulResourceType(val folderName: String, val reg: ValueRegistry<out Any>, val klazz: KClass<out Any>) : IResourceType {

    DECREES("decrees", DecreeRegistry, Decree::class),
    POOLS("pools", PoolRegistry, EntryPool::class);

}