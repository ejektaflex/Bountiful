package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.GameTime
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.world.World
import kotlin.math.max
import kotlin.random.Random

@Serializable
class BountyInfo(
    var tooltip: List<@Contextual MutableText> = listOf(Text.literal("DOOT")),
    var rarity: BountyRarity = BountyRarity.COMMON,
    var timeStarted: Long = -1L,
    var timeToComplete: Long = -1L,
    var objectiveFlags: Set<@Contextual Identifier> = emptySet(),
    var lastCached: Long = -1L
) {

    fun timeLeft(world: World): Long {
        return when (BountifulIO.configData.shouldBountiesHaveTimersAndExpire) {
            true -> max(timeStarted - world.time + timeToComplete, 0L)
            false -> 1L
        }
    }

    // ### Formatting ### //

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeft(world) / 20)
    }

    fun getOrRefreshTooltip(stack: ItemStack, worldTime: Long): List<MutableText> {
        if (worldTime - lastCached >= 20L) {
            BountyInfo[stack] = apply {
                tooltip = genTooltip(BountyData[stack])
                lastCached = worldTime
            }
        }
        return tooltip
    }

    private fun genTooltip(fromData: BountyData, player: ServerPlayerEntity): List<MutableText> {
        return buildList {
            add(Text.translatable("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.objectives.map {
                it.textSummary(player, true)
            })
            add(Text.translatable("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.rewards.map {
                it.textSummary(player, false)
            })
            add(Text.literal(Random.nextFloat().toString()))
        }
    }

    fun update(data: BountyData, player: ServerPlayerEntity): BountyInfo {
        objectiveFlags = data.objectives.map { it.logicId }.toSet()
        tooltip = genTooltip(data, player)
        //worldTime?.let { lastCached = it }
        return this
    }

    @Suppress("RemoveRedundantQualifierName")
    companion object : ItemDataJson<BountyInfo>() {
        override val identifier = Bountiful.id("bounty_info")
        override val ser = BountyInfo.serializer()
        override val default: () -> BountyInfo = { BountyInfo() }
    }

}