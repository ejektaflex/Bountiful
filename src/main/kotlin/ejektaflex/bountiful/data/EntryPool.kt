package ejektaflex.bountiful.data

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.IMerge
import ejektaflex.bountiful.api.data.IEntryPool
import ejektaflex.bountiful.api.data.entry.BountyEntry

// Currently unused? Should only need a PoolRegistry and a DecreeRegistry

open class EntryPool(@Expose override val id: String) : ValueRegistry<BountyEntry>(), IEntryPool, IMerge<EntryPool> {

    @Expose
    override var modsRequired: MutableList<String>? = null

    override fun merge(other: EntryPool) {
        content.addAll(other.content)
        modsRequired = other.modsRequired
    }

}