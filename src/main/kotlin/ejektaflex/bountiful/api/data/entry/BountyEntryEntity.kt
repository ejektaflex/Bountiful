package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.data.entry.feature.IAmount
import ejektaflex.bountiful.api.data.entry.feature.IKilledAmount
import ejektaflex.bountiful.api.ext.toEntityEntry
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.client.resources.I18n
import net.minecraft.entity.EntityType
import net.minecraft.nbt.CompoundNBT
import kotlin.math.ceil
import kotlin.math.max

class BountyEntryEntity : BountyEntry<BountyEntryEntity.EntityBountyFeatures>(), IBountyObjective {

    override var type: String = BountyType.Entity.id

    override val calculatedWorth: Int
        get() = unitWorth * feature.amount

    override fun pick(worth: Int?): BountyEntry<EntityBountyFeatures> {
        return cloned().apply {
            feature!!.amount = if (worth != null) {
                max(1, ceil(worth.toDouble() / unitWorth).toInt())
            } else {
                randCount
            }
        }
    }

    override val feature = EntityBountyFeatures()

    inner class EntityBountyFeatures : IAmount, IKilledAmount {
        override var amount: Int = 0
        override var killedAmount: Int = 0

        override fun deserializeNBT(tag: CompoundNBT) {
            super<IAmount>.deserializeNBT(tag)
            super<IKilledAmount>.deserializeNBT(tag)
        }

        override fun serializeNBT(tag: CompoundNBT) {
            super<IAmount>.serializeNBT(tag)
            super<IKilledAmount>.serializeNBT(tag)
        }
    }


    val entityEntry: EntityType<*>?
        get() {
            return content.toEntityEntry
        }

    override val prettyContent: String
        get() = ("(${feature.killedAmount}/${feature.amount}) §a" + "entity." + content + ".name" + " Kills§r")

    override fun toString(): String {
        return "BountyEntry (Entity) [Entity: $content, Amount: ${feature.amount}, Weight: $weight]"
    }


}