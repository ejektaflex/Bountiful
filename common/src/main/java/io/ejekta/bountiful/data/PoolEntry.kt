package io.ejekta.bountiful.data

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kudzu.KudzuVine
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Serializable
class PoolEntry private constructor() {
    var type: @Contextual Identifier = Identifier(Bountiful.ID, "null_pool")
    var rarity = BountyRarity.COMMON
    var content = "Nope"
    var name: String? = null
    private var icon: @Contextual Identifier? = null
    var amount = EntryRange(-1, -1)
    var unitWorth = -1000.0
    var weightMult = 1.0
    var timeMult = 1.0
    var repRequired = 0.0
    private val forbids: MutableList<ForbiddenContent> = mutableListOf()

    val protoPool: Pool?
        get() = BountifulContent.Pools.find { it.id == id.substringBefore('.') }

    val protoDecrees: List<Decree>
        get() = protoPool?.usedInDecrees ?: emptyList()

    fun isValid(server: MinecraftServer): Boolean {
        return try {
            val bountyType = BountyTypeRegistry[type]
            if (bountyType == null) {
                Bountiful.LOGGER.warn("Bounty Pool Entry has Invalid Type: (${id} - ${content}) details: ${save()}")
                return false
            }
            bountyType.isValid(this, server)
        } catch (e: Exception) {
            Bountiful.LOGGER.warn("Bounty Pool Entry Invalid: (${id} - ${content}) details: ${save()}")
            false
        }
    }

    @Transient lateinit var id: String

    val typeLogic: IBountyType?
        get() = BountyTypeRegistry[type]

    val conditions: JsonObject? = null

    var mystery: Boolean = false

    var nbt: @Contextual NbtCompound? = null

    val worthSteps: List<Double>
        get() = (amount.min..amount.max).map { it * unitWorth }

    val maxWorth: Double
        get() = amount.max * unitWorth

    fun save(format: Json = JsonFormats.DataPack) = format.encodeToString(serializer(), this)

    private fun getRelatedItems(world: ServerWorld): List<Item>? {
        return when (type) {
            BountyTypeRegistry.ITEM.id -> {
                val tagId = Identifier(content.substringAfter("#"))
                getTagItems(world.registryManager, getTagItemKey(tagId))
            }
            BountyTypeRegistry.ITEM_TAG.id -> {
                val tagId = Identifier(content)
                getTagItems(world.registryManager, getTagItemKey(tagId))
            }
            else -> null
        }
    }

    fun toEntry(world: ServerWorld, pos: BlockPos, worth: Double? = null, usedDecs: Set<String>? = emptySet()): BountyDataEntry {
        val amt = amountAt(worth)

        val actualContent = if (type == BountyTypeRegistry.ITEM.id && content.startsWith("#")) {
            val tagId = Identifier(content.substringAfter("#"))
            val items = getTagItems(world.registryManager, getTagItemKey(tagId))
            if (items.isEmpty()){
                Bountiful.LOGGER.warn("A pool entry tag has an empty list! $content")
                "minecraft:air"
            } else {
                items.random().identifier.toString()
            }
        } else {
            content
        }

        return BountyDataEntry.of(
            id,
            world,
            pos,
            type,
            actualContent,
            amountAt(worth),
            amt * unitWorth,
            nbt,
            name,
            icon,
            isMystery = false,
            rarity = rarity,
            critConditions = conditions
        ).apply {
            relatedDecreeIds = usedDecs ?: emptySet()
        }
    }

    private fun amountAt(worth: Double? = null): Int {
        val toGive = if (worth != null) {
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
        } || (!related.isNullOrEmpty()
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
    class ForbiddenContent(val type: @Contextual Identifier, val content: String)

    companion object {
        fun fromKudzu(kv: KudzuVine): PoolEntry {
            @Suppress("RemoveRedundantQualifierName")
            return JsonFormats.BlockEntity.decodeFromString(PoolEntry.serializer(), kv.toString())
        }

        // With encodeDefaults = false, we need a separate constructor
        fun create() = PoolEntry().apply {
            type = BountyTypeRegistry.ITEM.id
            amount = EntryRange(1, 1)
            content = "NO_CONTENT"
            unitWorth = 100.0
        }

    }

}