package io.ejekta.bountiful.data

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.bounty.logic.ItemTagLogic
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.identifier
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.StringNbtReader
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.ItemTags
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
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

    private fun getRelatedItems(world: ServerWorld): List<Item>? {
        return when (type) {
            BountyType.ITEM -> {
                val tagId = Identifier(content.substringAfter("#"))
                getTagItems(world, getTagItemKey(tagId))
            }
            BountyType.ITEM_TAG -> {
                val tagId = Identifier(content)
                getTagItems(world, getTagItemKey(tagId))
            }
            else -> null
        }
    }

    fun toEntry(world: ServerWorld, pos: BlockPos, worth: Double? = null): BountyDataEntry {
        val amt = amountAt(worth)

        val actualContent = if (type == BountyType.ITEM && content.startsWith("#")) {
            val tagId = Identifier(content.substringAfter("#"))
            val tags = getTagItems(world, getTagItemKey(tagId))
            if (tags.isEmpty()){
                Bountiful.LOGGER.warn("A pool entry tag has an empty list! $content")
                "minecraft:air"
            } else {
                val chosen = tags.random().identifier.toString()
                chosen
            }
        } else {
            content
        }

        return BountyDataEntry.of(
            world,
            pos,
            type,
            actualContent,
            amountAt(worth),
            amt * unitWorth,
            nbt,
            name,
            translation,
            isMystery = false,
            rarity = rarity
        )
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

    fun forbids(world: ServerWorld, entry: PoolEntry): Boolean {
        val related = getRelatedItems(world)
        return forbids.any {
            it.type == entry.type && it.content == entry.content
        } || (related != null
                    && related.isNotEmpty()
                    && related.any { it.identifier.toString() == entry.content }
                )
    }

    fun forbidsAny(world: ServerWorld, entries: List<PoolEntry>): Boolean {
        return entries.any { forbids(world, it) }
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