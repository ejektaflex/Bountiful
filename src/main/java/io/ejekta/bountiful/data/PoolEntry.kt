package io.ejekta.bountiful.data

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.config.JsonFormats
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.nbt.StringNbtReader
import net.minecraft.nbt.Tag
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Serializable
class PoolEntry private constructor() {
    var type = BountyType.NULL
    var rarity = BountyRarity.COMMON
    var content = "Nope"
    var amount = EntryRange(-1, -1)
    var unitWorth = -1000.0
    var weightMult = 1.0
    var timeMult = 1.0
    var repRequired = 0.0
    val forbid: MutableList<ForbiddenContent> = mutableListOf()

    var mystery: Boolean = false

    var nbt: String? = null

    var nbtData: Tag?
        get() = nbt?.let { StringNbtReader.parse(it) }
        set(value) {
            nbt = value?.asString()
        }

    fun save(format: Json = JsonFormats.DataPack) = format.encodeToString(serializer(), this)

    fun toEntry(worth: Double? = null): BountyDataEntry {
        val amt = amountAt(worth)
        return BountyDataEntry(type, content, amountAt(worth), nbt, isMystery = false, rarity = rarity).apply {
            this.worth = amt * unitWorth
        }
    }

    fun amountAt(worth: Double? = null): Int {
        var toGive = if (worth != null) {
            max(1, ceil(worth.toDouble() / unitWorth).toInt())
        } else {
            amount.pick()
        }
        // Clamp amount into amount range
        toGive = min(toGive, amount.max)
        toGive = max(toGive, amount.min)
        return toGive
    }

    val worthRange: Pair<Double, Double>
        get() = (amount.min * unitWorth) to (amount.max * unitWorth)

    fun worthDistanceFrom(value: Double): Int {
        val rnge = worthRange
        return if (value >= rnge.first && value <= rnge.second) {
            0
        } else {
            min(abs(rnge.first - value), abs(rnge.second - value)).toInt()
        }
    }

    fun forbids(entry: PoolEntry): Boolean {
        return forbid.any { it.type == entry.type && it.content == entry.content }
    }

    fun forbidsAny(entries: List<PoolEntry>): Boolean {
        return entries.any { forbids(it) }
    }

    @Serializable
    class EntryRange(val min: Int, val max: Int) {
        fun pick(): Int = (min..max).random()
        override fun toString() = "[$min - $max]"
    }

    @Serializable
    class ForbiddenContent(val type: BountyType, val content: String)

    companion object {

        // With encodeDefaults = false, we need a separate constructor
        fun create() = PoolEntry().apply {
            type = BountyType.ITEM
            amount = EntryRange(1, 1)
            content = "NO_CONTENT"
            unitWorth = 100.0
        }

    }

}