package ejektaflex.bountiful.data

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.api.data.IEntryPool
import ejektaflex.bountiful.api.data.entry.BountyEntry
import ejektaflex.bountiful.api.data.entry.feature.IEntryFeature
import net.minecraft.world.World

// Currently unused? Should only need a PoolRegistry and a DecreeRegistry

open class EntryPool(@Expose override val id: String) : ValueRegistry<BountyEntry>(), IEntryPool {

    @Expose
    override val modsRequired: MutableList<String>? = null

}