package ejektaflex.bountiful.api.logic.pickable

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toEntityEntry
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.fml.common.registry.EntityEntry

class PickedEntryEntity(
        @Expose(serialize = false, deserialize = false)
        val genericPick: PickedEntry
) : IPickedEntry by genericPick {

    var killedAmount = 0

    override fun deserializeNBT(tag: NBTTagCompound) {
        genericPick.deserializeNBT(tag)
        killedAmount = tag.getInteger("killedAmount")
    }

    override fun serializeNBT(): NBTTagCompound {
        return genericPick.serializeNBT().apply {
            setInteger("killedAmount", killedAmount)
        }
    }

    val entityEntry: EntityEntry?
        get() {
            return content.toEntityEntry
        }

    override val contentObj: Any?
        get() = entityEntry

    override val prettyContent: String
        get() = ("($killedAmount/$amount) " + entityEntry?.name + " Kills")

    override fun toString(): String {
        return "$amount x ${entityEntry?.name}"
    }

}