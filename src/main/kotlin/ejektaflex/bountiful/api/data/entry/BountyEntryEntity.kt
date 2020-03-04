package ejektaflex.bountiful.api.data.entry

import com.google.gson.annotations.Expose
import ejektaflex.bountiful.api.ext.toEntityType
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import kotlin.math.ceil
import kotlin.math.max

class BountyEntryEntity : BountyEntry(), IBountyObjective {

    @Expose
    override var type: String = BountyType.Entity.id

    var killedAmount = 0

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override fun validate() {
        // TODO Implement entity validation
    }

    override fun deserializeNBT(tag: CompoundNBT) {
        super.deserializeNBT(tag)
        killedAmount = tag.getInt("killedAmount")
    }


    override fun serializeNBT(): CompoundNBT {
        return super.serializeNBT().apply {
            putInt("killedAmount", killedAmount)
        }
    }

    fun isSameEntity(e: LivingEntity): Boolean {
        val ereg = e.type.registryName
        if (ereg.toString() == content) {
            return true
        }
        return false
    }

    override val formattedName: ITextComponent
        get() = (content.toEntityType?.name ?: StringTextComponent(content))

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return StringTextComponent(progress.color).appendSibling(
                formattedName
        ).appendSibling(
                StringTextComponent(" Kills§r ")
        ).appendSibling(
                StringTextComponent("§f${progress.stringNums}")
        )
    }

    override fun toString(): String {
        return "BountyEntry ($type) [Entity: $content, Amount: ${amount}, Weight: $weight]"
    }


}