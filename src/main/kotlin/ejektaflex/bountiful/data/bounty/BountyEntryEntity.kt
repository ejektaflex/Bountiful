package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.toEntityType
import ejektaflex.bountiful.ext.withSibling
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.text.*
import net.minecraftforge.registries.ForgeRegistries

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

    override val formattedName: IFormattableTextComponent
        get() = TranslationTextComponent(
            ForgeRegistries.ENTITIES.entries.find {
                it.key.location.toString() == content
            }?.value?.translationKey ?: "entity.generic.name"
        )

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return StringTextComponent("").withSibling(
                formattedName.mergeStyle(progress.color)
        ).withSibling(
                StringTextComponent(" ")
        ).withSibling(
                TranslationTextComponent("bountiful.bounty.type.entity.kills").mergeStyle(progress.color)
        ).withSibling(
                StringTextComponent(" ")
        ).withSibling(
                StringTextComponent(progress.stringNums).mergeStyle(TextFormatting.WHITE)
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Entity: $content, Amount: ${amount}, Weight: $weight]"
    }


}