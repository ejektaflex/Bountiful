package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.logic.IEntryLogic
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(
    val type: BountyType,
    val content: String,
    val amount: Int,
    var nbt: @Contextual NbtCompound? = null,
    var name: String? = null,
    var translation: String? = null,
    var isMystery: Boolean = false,
    var rarity: BountyRarity = BountyRarity.COMMON,
    var extra: Int = 0 // Used to track extra data, e.g. current progress if needed
) {

    val logic: IEntryLogic
        get() = type.logic(this)

    @Transient
    var worth = Double.MIN_VALUE

    override fun toString(): String {
        return "BDE[type=$type, content=$content, amount=$amount, isNbtNull=${nbt == null}, name=$name, mystery=$isMystery]"
    }

    fun formatted(data: BountyData, player: PlayerEntity, isObj: Boolean): Text {
        return when (isMystery) {
            true -> LiteralText("???").formatted(Formatting.BOLD).append(
                LiteralText("x$amount").formatted(Formatting.WHITE)
            )
            false -> logic.format(isObj, player)
        }

    }

}