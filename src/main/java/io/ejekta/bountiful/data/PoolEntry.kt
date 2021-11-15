package io.ejekta.bountiful.data

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemTagLogic
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.kambrik.ext.identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.tag.ItemTags
import net.minecraft.util.Identifier
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Serializable
class PoolEntry private constructor() {
    var type = BountyType.NULL
    var rarity = BountyRarity.COMMON
    var content = "Nope"
    var name: String? = null
    var translation: String? = null
    var amount = EntryRange(-1, -1)
    var unitWorth = -1000.0
    var weightMult = 1.0
    var timeMult = 1.0
    var repRequired = 0.0
    private val forbids: MutableList<ForbiddenContent> = mutableListOf()

    @Transient val sources: MutableSet<String> = mutableSetOf()

    var mystery: Boolean = false

    var nbt: @Contextual NbtCompound? = null

    val worthSteps: List<Double>
        get() = (amount.min..amount.max).map { it * unitWorth }

    fun save(format: Json = JsonFormats.DataPack) = format.encodeToString(serializer(), this)

    private fun getRelatedItems(): List<Item>? {
        return when (type) {
            BountyType.ITEM -> {
                val tagId = Identifier(content.substringAfter("#"))
                val tag = ItemTags.getTagGroup().getTag(tagId)
                tag?.values()
            }
            BountyType.ITEM_TAG -> {
                val tagId = Identifier(content)
                val tag = ItemTags.getTagGroup().getTag(tagId)
                tag?.values()
            }
            else -> null
        }
    }

    fun toEntry(worth: Double? = null): BountyDataEntry {
        val amt = amountAt(worth)

        val actualContent = if (type == BountyType.ITEM && content.startsWith("#")) {
            val tagId = Identifier(content.substringAfter("#"))
            val tag = ItemTags.getTagGroup().getTag(tagId)
            if (tag == null) {
                Bountiful.LOGGER.warn("A pool entry required a tag that does not exist: $content")
                content
            } else if (tag.values().isEmpty()){
                Bountiful.LOGGER.warn("A pool entry tag has an empty list! $content")
                content
            } else {
                val chosen = tag.values().random().identifier.toString()
                chosen
            }
        } else {
            content
        }

        return BountyDataEntry(
            type,
            actualContent,
            amountAt(worth),
            nbt,
            name,
            translation,
            isMystery = false,
            rarity = rarity
        ).apply {
            this.worth = amt * unitWorth
        }
    }

    private fun amountAt(worth: Double? = null): Int {
        var toGive = if (worth != null) {
            max(1, ceil(worth.toDouble() / unitWorth).toInt())
        } else {
            amount.pick()
        }.coerceIn(amount.min..amount.max) // Clamp amount into amount range
        return toGive
    }

    private val worthRange: Pair<Double, Double>
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
        val related = getRelatedItems()
        return forbids.any {
            it.type == entry.type && it.content == entry.content
        } || (related != null
                    && related.isNotEmpty()
                    && related.any { it.identifier.toString() == entry.content }
                )
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