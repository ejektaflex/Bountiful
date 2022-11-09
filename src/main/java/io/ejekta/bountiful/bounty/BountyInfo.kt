package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.kambrik.serial.ItemDataJson
import io.ejekta.kambrik.text.textTranslate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Serializable
class BountyInfo private constructor(
    var tooltip: List<@Contextual MutableText> = listOf(Text.literal("DOOT")),
    var rarity: BountyRarity = BountyRarity.COMMON,
    var timeStarted: Long = 0,
    var objectiveFlags: List<Int> = emptyList()
) {

    fun newTooltipInfo(fromData: BountyData): List<MutableText> {
        return buildList {
            add(Text.translatable("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.objectives.map {
                it.textSummary(fromData, MinecraftClient.getInstance().player!!, true)
            })
            add(Text.translatable("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.rewards.map {
                it.textSummary(fromData, MinecraftClient.getInstance().player!!, false)
            })
        }
    }

    @Suppress("RemoveRedundantQualifierName")
    companion object : ItemDataJson<BountyInfo>() {
        override val identifier = Bountiful.id("bounty_info")
        override val ser = BountyInfo.serializer()
        override val default: () -> BountyInfo = { BountyInfo() }

        fun cacheWithData(stack: ItemStack, data: BountyData) {
            set(stack, fromBountyData(data))
        }

        private fun fromBountyData(data: BountyData): BountyInfo {
            return BountyInfo().apply {
                rarity = data.rarity
                objectiveFlags = data.objectives.map { it.type.ordinal }
                timeStarted = data.timeStarted
                // TODO tooltip!
                tooltip = newTooltipInfo(data)
            }
        }
    }

}