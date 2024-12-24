package io.ejekta.bountiful.components

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import net.minecraft.ChatFormatting
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

// Tracks the status of a given bounty
@Serializable @JvmRecord
data class BountyDataEntry(
    val id: String,
    val logicId: @Contextual ResourceLocation,
    val content: String,
    val amount: Int,
    val worth: Int,
    val nbt: @Contextual CompoundTag? = null,
    val name: String? = null,
    val icon: @Contextual ResourceLocation? = null,
    val isMystery: Boolean = false,
    val rarity: BountyRarity = BountyRarity.COMMON,
    val critConditions: JsonObject? = null,
    val relatedDecreeIds: Set<String> = emptySet()
) {

    private fun getRelatedDecrees(): Set<Decree> {
        return BountifulContent.getDecrees(relatedDecreeIds)
    }

    fun getRelatedProfessions(): Set<String> {
        return getRelatedDecrees().map { it.linkedProfessions }.flatten().toSet()
    }

    val translation: MutableComponent
        get() = Component.translatable("bountiful.entry.${id}")

    val logic: IBountyType
        get() = BountyTypeRegistry[logicId]!!

    override fun toString(): String {
        return "BDE[type=$logic, content=$content, amount=$amount, isNbtNull=${nbt == null}, name=$name, mystery=$isMystery]"
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

}