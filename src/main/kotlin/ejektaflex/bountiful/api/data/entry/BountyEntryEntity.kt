package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toEntityEntry
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT

class BountyEntryEntity : BountyEntry() {

    override val type: String = BountyType.Entity.id

    @Expose(serialize = false, deserialize = false)
    var killedAmount = 0

    override fun deserializeNBT(tag: CompoundNBT) {
        super.deserializeNBT(tag)
        killedAmount = tag.getInt("killedAmount")
    }

    override fun serializeNBT(): CompoundNBT {
        return super.serializeNBT().apply {
            putInt("killedAmount", killedAmount)
        }
    }

    val entityEntry: EntityType<*>?
        get() {
            return content.toEntityEntry
        }

    override val contentObj: EntityType<*>?
        get() = entityEntry

    override val prettyContent: String
        get() = ("($killedAmount/$amount) §a" + I18n.format("entity." + entityEntry?.registryName + ".name") + " Kills§r")

    override fun toString(): String {
        return "BountyEntry (Entity) [Entity: $content, Amount: $amount, Weight: $weight]"
    }


}