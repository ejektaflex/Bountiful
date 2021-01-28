package io.ejekta.bountiful.common.bounty

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.nbt.CompoundTag

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(val type: BountyType, val content: String, val amount: Int) {
    // , @Contextual val nbt: CompoundTag
}