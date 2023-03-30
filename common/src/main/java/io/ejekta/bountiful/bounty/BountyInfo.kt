package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.util.GameTime
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.max

@Serializable
class BountyInfo(
    var rarity: BountyRarity = BountyRarity.COMMON,
    var timeStarted: Long = -1L,
    var timeToComplete: Long = -1L
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

    fun genTooltip(fromData: BountyData, isServer: Boolean): List<MutableText> {
        if (isServer) {
            return emptyList()
        }
        val player = MinecraftClient.getInstance().player!!
        return buildList {
            add(Text.translatable("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.objectives.map {
                it.textSummary(player, true)
            })
            add(Text.translatable("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":"))
            addAll(fromData.rewards.map {
                it.textSummary(player, false)
            })
        }
    }

    @Suppress("RemoveRedundantQualifierName")
    companion object : ItemDataJson<BountyInfo>() {
        override val identifier = Bountiful.id("bounty_info")
        override val ser = BountyInfo.serializer()
        override val default: () -> BountyInfo = { BountyInfo() }
    }

}