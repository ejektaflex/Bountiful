package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.toEntityType
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent

class BountyEntryEntity : BountyEntry(), IBountyObjective {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Entity.id

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
                StringTextComponent(" ")
        ).appendSibling(
                TranslationTextComponent("bountiful.bounty.type.entity.kills").applyTextStyle(TextFormatting.RESET)
        ).appendSibling(
                StringTextComponent(" ")
        ).appendSibling(
                StringTextComponent(progress.stringNums).applyTextStyle {
                    it.color = TextFormatting.WHITE
                }
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Entity: $content, Amount: ${amount}, Weight: $weight]"
    }


}