package io.ejekta.bountiful.common.bounty

import kotlinx.serialization.Serializable
import net.minecraft.util.Rarity

@Serializable
abstract class BountyData {
    var timeCreated = 0L
    var timeTaken = 0L
    val rarity = Rarity.COMMON
    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()
}