package io.ejekta.bountiful.common.bounty

import io.ejekta.bountiful.common.config.Format
import io.ejekta.bountiful.common.util.GameTime
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World
import kotlin.math.max

@Serializable
class BountyData {

    var owner: String? = null // UUID
    var timeStarted = -1L
    var timeToComplete = -1L
    var rarity = BountyRarity.COMMON
    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()

    private fun timeLeft(world: World): Long {
        return max(timeStarted - world.time + timeToComplete, 0L)
    }

    fun save() = Format.NBT.encodeToJsonElement(serializer(), this)

    fun tryCashIn(player: PlayerEntity, stack: ItemStack): Boolean {
        val objs = objectives.map {
            it().finishObjective(this, it, player)
        }

        return if (objs.all { it }) {
            rewards.forEach {
                it().giveReward(this, it, player)
            }
            stack.decrement(stack.maxCount)
            true
        } else {
            player.sendMessage(TranslatableText("bountiful.tooltip.requirements"), false)
            println("All objectives finished but some returned false!")
            false
        }

    }

    override fun toString(): String {
        return Format.DataPack.encodeToString(ser, this)
    }

    // ### Formatting ### //

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeft(world) / 20)
    }

    private fun formattedObjectives(): List<Text> {
        return objectives.map {
            it.formatted(this, MinecraftClient.getInstance().player!!, true)
        }
    }

    private fun formattedRewards(): List<Text> {
        return rewards.map {
            it.formatted(this, MinecraftClient.getInstance().player!!, false)
        }
    }

    fun tooltipInfo(world: World): List<Text> {
        val lines = mutableListOf<Text>()
        lines += TranslatableText("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":")
        lines += formattedObjectives()
        lines += TranslatableText("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":")
        lines += formattedRewards()
        return lines
    }

    companion object : ItemData<BountyData>() {
        override val ser = BountyData.serializer()
        override val creator: () -> BountyData = { BountyData() }
    }

}