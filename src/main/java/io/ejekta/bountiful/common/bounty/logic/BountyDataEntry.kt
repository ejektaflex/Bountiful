package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.bounty.logic.entry.EntryItemLogic
import io.ejekta.bountiful.common.bounty.logic.entry.IEntryLogic
import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import net.minecraft.text.Text

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(val type: BountyType, val content: String, val amount: Int, var nbt: String? = null) {

    var nbtData: Tag?
        get() = nbt?.let { StringNbtReader.parse(it) }
        set(value) {
            nbt = value?.asString()
        }

    operator fun invoke() = type.logic

    fun formatted(data: BountyData, player: PlayerEntity, isObj: Boolean): Text {
        val progress = type.logic.getProgress(data, this, player)
        return type.logic.format(this, isObj, progress)
    }

}