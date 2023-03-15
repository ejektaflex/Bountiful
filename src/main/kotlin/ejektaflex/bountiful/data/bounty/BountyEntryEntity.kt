package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.withSibling
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraftforge.registries.ForgeRegistries

class BountyEntryEntity : BountyEntry(), IBountyObjective {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Entity.id

    var killedAmount = 0

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override fun validate() {
        // TODO Implement entity validation (this is an old todo)
    }

    override fun deserializeNBT(tag: CompoundTag) {
        super.deserializeNBT(tag)
        killedAmount = tag.getInt("killedAmount")
    }


    override fun serializeNBT(): CompoundTag {
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

    override val formattedName: MutableComponent
        get() = Component.translatable(
            ForgeRegistries.ENTITIES.entries.find {
                it.key.location.toString() == content
            }?.value?.translationKey ?: "entity.generic.name"
        )

    override fun tooltipObjective(progress: BountyProgress): Component {
        return Component.literal("").withSibling(
                formattedName.withStyle(progress.color)
        ).withSibling(
                Component.literal(" ")
        ).withSibling(
                Component.translatable("bountiful.bounty.type.entity.kills").withStyle(progress.color)
        ).withSibling(
                Component.literal(" ")
        ).withSibling(
                Component.literal(progress.stringNums).withStyle(ChatFormatting.WHITE)
        )
    }

    override fun toString(): String {
        return "BountyEntry ($bType) [Entity: $content, Amount: ${amount}, Weight: $weight]"
    }


}