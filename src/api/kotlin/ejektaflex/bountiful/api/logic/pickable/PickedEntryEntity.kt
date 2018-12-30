package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toEntityEntry
import net.minecraftforge.fml.common.registry.EntityEntry

class PickedEntryEntity(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {

    val entityEntry: EntityEntry?
        get() {
            return contentID.toEntityEntry
        }

    override val content: Any?
        get() = entityEntry

    override val prettyContent: String
        get() = ("Kill " + entityEntry?.name)

    override fun toString(): String {
        return "$amount x ${entityEntry?.name}"
    }

}