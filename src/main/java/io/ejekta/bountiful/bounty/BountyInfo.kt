package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.nbt.NbtCompound

@Serializable
class BountyInfo(
    var tooltip: @Contextual NbtCompound = NbtCompound(),
    var rarity: BountyRarity = BountyRarity.COMMON,
    var timeStarted: Long = 0,
    var objectiveFlags: List<Int> = emptyList()
) {

    fun fromBountyData(data: BountyData) {
        rarity = data.rarity
        objectiveFlags = data.objectives.map { it.type.ordinal }
        timeStarted = data.timeStarted
    }

    @Suppress("RemoveRedundantQualifierName")
    companion object : ItemDataJson<BountyInfo>() {
        override val identifier = Bountiful.id("bounty_info")
        override val ser = BountyInfo.serializer()
        override val default: () -> BountyInfo = { BountyInfo() }
    }

}