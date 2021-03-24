package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.logic.IEntryLogic
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(
    val type: BountyType,
    val content: String,
    val amount: Int,
    var nbt: String? = null,
    var name: String? = null,
    var isMystery: Boolean = false,
    var rarity: BountyRarity = BountyRarity.COMMON
) : IEntryLogic by type.logic { // TODO find out if this will error along with serialization?
    // TODO perhaps make it into `type.logicFor(this)` but this might be just the companion object

    @Transient
    var worth = Double.MIN_VALUE

    var nbtData: Tag?
        get() = nbt?.let { StringNbtReader.parse(it) }
        set(value) {
            nbt = value?.asString()
        }

    override fun toString(): String {
        return "BDE[type=$type, content=$content, amount=$amount, isNbtNull=${nbt == null}, name=$name, mystery=$isMystery]"
    }

    operator fun invoke() = type.logic

    fun formatted(data: BountyData, player: PlayerEntity, isObj: Boolean): Text {
        return when (isMystery) {
            true -> LiteralText("???").formatted(Formatting.BOLD).append(
                LiteralText("x$amount").formatted(Formatting.WHITE)
            )
            false -> {
                val progress = type.logic.getProgress(this, player)
                type.logic.format(this, isObj, progress)
            }
        }

    }

}