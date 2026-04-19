package io.ejekta.bountiful.components

import com.google.gson.JsonPrimitive
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.content.BountifulContent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player

typealias GsonObject = com.google.gson.JsonObject
typealias GsonElement = com.google.gson.JsonElement


// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(
    val id: String,
    val content: String,
    val rarity: BountyRarity = BountyRarity.COMMON,
    val logicName: String = "item",
    val amount: Int = -99,
    val name: String? = null,
    var data: @Contextual GsonObject? = null,
) {

    // Icon is local (to the client) rather than stored in items for net performance
    val icon: Identifier? by lazy {
        BountifulContent.PoolEntryMap[id]?.icon
    }

    // Criteria is local (to the server) - why store in the item when it's only evaluated on the server?
    val criteriaJson: GsonObject? by lazy {
        BountifulContent.PoolEntryMap[id]?.conditions
    }

    val isMystery: Boolean = false

    val logic: IBountyType?
        get() = BountyTypeRegistry.getOptional(Identifier.parse(logicName)).orElse(null)

    fun getRelatedProfessions(): Set<String> {
        val poolId = id.substringBefore(".")
        return BountifulContent.Pools
            .find { it.id == poolId }
            ?.usedInDecrees
            ?.flatMap { it.linkedProfessions }
            ?.toSet()
            ?: emptySet()
    }

    fun contentToTranslationKey(): String {
        return content.replace(":", ".").replace("/", ".")
    }

    val translation: MutableComponent
        get() {
            val key = "tag.item.${contentToTranslationKey()}"
            val fallback = content.substringAfter(":")
                .split("/")
                .reversed()
                .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
            return Component.translatableWithFallback(key, fallback)
        }

    override fun toString(): String {
        return "BDE[type=$logic, content=$content, amount=$amount, name=$name, mystery=$isMystery]"
    }

    fun textOnBoardSidebar(player: Player): List<Component> {
        return logic?.textOnBoardSidebar(this, player) ?: emptyList()
    }

    fun textOnBounty(player: Player, isObj: Boolean, current: Int): List<MutableComponent> {
        return when (isMystery) {
            true -> listOf( Component.literal("???").withStyle(ChatFormatting.BOLD).append(
                Component.literal("x$amount").withStyle(rarity.color)
            ) )
            false -> logic?.textOnBounty(this, isObj, player, current) ?: emptyList()
        }
    }

}
