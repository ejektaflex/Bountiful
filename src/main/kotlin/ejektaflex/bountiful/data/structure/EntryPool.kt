package ejektaflex.bountiful.data.structure

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.util.IMerge
import ejektaflex.bountiful.data.bounty.BountyEntry
import ejektaflex.bountiful.util.IIdentifiable
import ejektaflex.bountiful.util.ValueRegistry
import net.minecraftforge.fml.ModList

// Currently unused? Should only need a PoolRegistry and a DecreeRegistry

open class EntryPool() : ValueRegistry<BountyEntry>(), IMerge<EntryPool>, IIdentifiable {

    override var id: String = "UNKNOWN_POOL_ID"

    @Expose
    var modsRequired: MutableList<String>? = null

    override val canLoad: Boolean
        get() {
            return if (modsRequired == null) {
                true
            } else {
                modsRequired!!.all { ModList.get().isLoaded(it) }
            }
        }

    override fun merge(other: EntryPool) {
        content.addAll(other.content)
        modsRequired = other.modsRequired
    }


}