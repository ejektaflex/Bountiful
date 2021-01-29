package io.ejekta.bountiful.common.bounty

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(val type: BountyType, val content: String, val amount: Int, var nbt: String? = null) {

    var nbtData: Tag?
        get() = nbt?.let { StringNbtReader.parse(it) }
        set(value) {
            nbt = value?.asString()
        }

}