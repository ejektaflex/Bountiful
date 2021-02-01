package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.bounty.logic.entry.EntryItemLogic
import io.ejekta.bountiful.common.bounty.logic.entry.IEntryLogic
import kotlinx.serialization.Serializable
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
    var isMystery: Boolean = false
) {

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
                val progress = type.logic.getProgress(data, this, player)
                type.logic.format(this, isObj, progress)
            }
        }

    }

}