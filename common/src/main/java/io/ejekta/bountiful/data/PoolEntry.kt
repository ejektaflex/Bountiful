package io.ejekta.bountiful.data

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeCriteria
import io.ejekta.bountiful.bounty.types.builtin.BountyTypeItem
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.components.GsonObject
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.util.getTagItemKey
import io.ejekta.bountiful.util.getTagItems
import io.ejekta.kambrik.ext.id
import io.ejekta.kudzu.KudzuVine
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.Item
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Serializable
class PoolEntry private constructor() {
    var type: @Contextual ResourceLocation = ResourceLocation.fromNamespaceAndPath(Bountiful.ID, "null_pool")
    var rarity = BountyRarity.COMMON
    var content = "Nope"
    var name: String? = null
    var icon: @Contextual ResourceLocation? = null
        private set
    var amount = EntryRange(-1, -1)
    var unitWorth = -1000.0
    var weightMult = 1.0
    var timeMult = 1.0
    var repRequired = 0.0
    private val forbids: MutableList<ForbiddenContent> = mutableListOf()

    // TODO will this fail if the user's resource file has a '.' in it?
    val protoPool: Pool?
        get() = BountifulContent.PoolMap[id.substringBefore('.')]

    val protoDecrees: List<Decree>
        get() = protoPool?.usedInDecrees ?: emptyList()

    @Transient var isValidCache: Boolean? = null

    fun isValid(server: MinecraftServer): Boolean {
        if (isValidCache != null) {
            return isValidCache!!
        } else {
            isValidCache = try {
                val bountyType = BountyTypeRegistry[type]
                if (bountyType == null) {
                    Bountiful.LOGGER.warn("Bounty Pool Entry has Invalid Type: (${id} - ${content}) details: ${save()}")
                    return false
                }
                bountyType.isValid(this, server)
            } catch (e: Exception) {
                Bountiful.LOGGER.warn("Bounty Pool Entry Invalid: (${id} - ${content}) details: ${save()}")
                false
            }.also {
                return it
            }
        }
    }

    @Transient lateinit var id: String

    val typeLogic: IBountyType?
        get() = BountyTypeRegistry[type]

    val conditions: @Contextual GsonObject? = null
    var components: @Contextual GsonObject? = null

    var mystery: Boolean = false

    //var nbt: @Contextual CompoundTag? = null

    val worthSteps: List<Double>
        get() = (amount.min..amount.max).map { it * unitWorth }

    val maxWorth: Double
        get() = amount.max * unitWorth

    fun save(): String {
        return JsonFormats.Hand.dynamicEncodeToString(this, serializer())
    }

    @Transient
    private var relatedItemCache: List<Item>? = null

    private fun getRelatedItems(world: ServerLevel): List<Item>? {
        if (relatedItemCache != null) {
            return relatedItemCache
        } else {
            relatedItemCache = when (type) {
                BountyTypeRegistry.ITEM.id -> {
                    val tagId = ResourceLocation.parse(content.substringAfter("#"))
                    getTagItems(world.registryAccess(), getTagItemKey(tagId))
                }
                BountyTypeRegistry.ITEM_TAG.id -> {
                    val tagId = ResourceLocation.parse(content)
                    getTagItems(world.registryAccess(), getTagItemKey(tagId))
                }
                else -> emptyList()
            }
        }
        return relatedItemCache
    }

    fun toEntry(world: ServerLevel, pos: BlockPos, worth: Double? = null, usedDecs: Set<String>? = emptySet()): BountyCreator.ValuedEntry {
        val amt = amountAt(worth)

        val actualContent = if (type == BountyTypeRegistry.ITEM.id && content.startsWith("#")) {
            val tagId = ResourceLocation.parse(content.substringAfter("#"))
            val items = getTagItems(world.registryAccess(), getTagItemKey(tagId))
            if (items.isEmpty()){
                Bountiful.LOGGER.warn("A pool entry tag has an empty list! $content")
                "minecraft:air"
            } else {
                items.random().id.toString()
            }
        } else {
            content
        }

        val totWorth = amt * unitWorth

        val entry = BountyDataEntry(
            id,
            content = actualContent,
            rarity,
            type.toString(),
            amt,
            name = name,
            data = when (typeLogic) {
                is BountyTypeCriteria -> conditions
                is BountyTypeItem -> components
                else -> null
            }
            // TODO remember no more related decree ids here, need to get dynamically
        )

        return BountyCreator.ValuedEntry(entry, totWorth)
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

    fun forbids(world: ServerLevel, entry: PoolEntry): Boolean {
        val related = getRelatedItems(world)
        return forbids.any {
            it.type == entry.type && it.content == entry.content
        } || (!related.isNullOrEmpty()
                    && related.any { it.id.toString() == entry.content }
                )
    }

    fun forbidsAny(world: ServerLevel, entries: List<PoolEntry>): Boolean {
        return entries.any { forbids(world, it) }
    }

    @Serializable
    class EntryRange(val min: Int, val max: Int) {
        fun pick(): Int = (min..max).random()
        override fun toString() = "[$min - $max]"
    }

    @Serializable
    class ForbiddenContent(val type: @Contextual ResourceLocation, val content: String)

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