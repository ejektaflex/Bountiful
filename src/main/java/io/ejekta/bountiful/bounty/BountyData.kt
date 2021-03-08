package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.util.GameTime
import io.ejekta.kambrikx.api.nbt.ItemData
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.max

@Suppress("RemoveRedundantQualifierName")
@Serializable
class BountyData {

    var owner: String? = null // UUID
    var timeStarted = -1L
    var timeToComplete = -1L
    var rarity = BountyRarity.COMMON
    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()

    private fun timeLeft(world: World): Long {
        return when (BountifulIO.configData.shouldBountiesHaveTimersAndExpire) {
            true -> max(timeStarted - world.time + timeToComplete, 0L)
            false -> 1L
        }
    }

    fun tryCashIn(player: PlayerEntity, stack: ItemStack): Boolean {

        if (timeLeft(player.world) <= 0) {
            player.sendMessage(TranslatableText("bountiful.bounty.expired"), false)
            return false
        }

        val objs = objectives.map {
            it().finishObjective(this, it, player)
        }

        return if (objs.all { it }) {
            rewards.forEach { it().giveReward(this, it, player) }
            stack.decrement(stack.maxCount)
            true
        } else {
            player.sendMessage(TranslatableText("bountiful.tooltip.requirements"), false)
            false
        }

    }

    override fun toString(): String {
        return JsonFormats.DataPack.encodeToString(ser, this)
    }

    // ### Formatting ### //

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeft(world) / 20)
    }

    fun tooltipInfo(world: World): List<Text> {
        val lines = mutableListOf<Text>()
        lines += TranslatableText("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":")
        lines += objectives.map {
            it.formatted(this, MinecraftClient.getInstance().player!!, true)
        }
        lines += TranslatableText("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":")
        lines += rewards.map {
            it.formatted(this, MinecraftClient.getInstance().player!!, false)
        }
        return lines
    }

    companion object : ItemData<BountyData>() {
        override val identifier = Bountiful.id("bounty_data")
        override val ser = BountyData.serializer()
        override val default: () -> BountyData = { BountyData() }
    }

}