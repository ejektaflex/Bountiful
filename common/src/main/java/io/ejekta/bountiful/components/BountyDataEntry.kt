package io.ejekta.bountiful.components

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import io.ejekta.percale.reverse.GsonObjectSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

typealias GsonObject = com.google.gson.JsonObject


// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry(
    val id: String,
    val flags: Int,
    val content: String,
    val name: String? = null,
    //val data: @Contextual GsonObject? = null
) {

    // Icon is local (to the client) rather than stored in items for net performance
    val icon: ResourceLocation? by lazy {
        BountifulContent.PoolEntryMap[id]?.icon
    }

    // Criteria is local (to the server) - why store in the item when it's only evaluated on the server?
    val criteriaJson: GsonObject? by lazy {
        BountifulContent.PoolEntryMap[id]?.conditions
    }

    val isMystery: Boolean by lazy { flags.toUInt().getUnsafeBits(31, 1) == 1u }

    val rarity: BountyRarity by lazy { BountyRarity.entries[flags.toUInt().getUnsafeBits(28, 3).toInt()] }

    val logic: IBountyType by lazy { BountyTypeRegistry.fromIntId(flags.toUInt().getUnsafeBits(24, 4).toInt()) }

    val amount: Int by lazy { flags.toUInt().getUnsafeBits(0, 24).toInt() }

    private fun getRelatedDecrees(): Set<Decree> {
        return emptySet()
        // TODO grab based on id
        //return BountifulContent.getDecrees(relatedDecreeIds)
    }

    fun getRelatedProfessions(): Set<String> {
        return getRelatedDecrees().map { it.linkedProfessions }.flatten().toSet()
    }

    val translation: MutableComponent
        get() = Component.translatable("bountiful.entry.${id}")


    override fun toString(): String {
        return "BDE[type=$logic, content=$content, amount=$amount, name=$name, mystery=$isMystery]"
    }

    fun textOnBoardSidebar(player: Player): List<Component> {
        return logic.textOnBoardSidebar(this, player)
    }

    fun textOnBounty(player: Player, isObj: Boolean, current: Int): MutableComponent {
        return when (isMystery) {
            true -> Component.literal("???").withStyle(ChatFormatting.BOLD).append(
                Component.literal("x$amount").withStyle(ChatFormatting.WHITE)
            )
            false -> logic.textOnBounty(this, isObj, player, current)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    companion object {

        fun packFlags(inMystery: Boolean, inRarity: BountyRarity, inLogic: ResourceLocation, inAmt: Int): Int {
            val mysteryNum = 0u.putUnsafeBits(31, if (inMystery) 1u else 0u)
            val rarityNum = 0u.putUnsafeBits(28, inRarity.ordinal.toUInt())
            val logicNumSigned = BountyTypeRegistry.toIntId(BountyTypeRegistry.get(inLogic)!!)
            val logicNum = 0u.putUnsafeBits(24, logicNumSigned.toUInt())
            val amtNum = 0u.putUnsafeBits(0, inAmt.toUInt())
            return (mysteryNum + rarityNum + logicNum + amtNum).toInt()
        }

        // Warning: these can overflow and do not do bounds checking
        inline fun UInt.getUnsafeBits(index: Int, size: Int): UInt {
            return ((this shr index) and (UInt.MAX_VALUE shr (index + size)))
        }

        inline fun UInt.putUnsafeBits(index: Int, value: UInt): UInt {
            return this + (value shl index)
        }

    }

}