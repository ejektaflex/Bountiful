package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.util.GameTime
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
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

    fun timeLeft(world: World): Long {
        return when (BountifulIO.configData.shouldBountiesHaveTimersAndExpire) {
            true -> max(timeStarted - world.time + timeToComplete, 0L)
            false -> 1L
        }
    }

    private fun hasFinishedObjectives(player: PlayerEntity): Boolean {
        return objectives.all {
            it.tryFinishObjective(player)
        }
    }

    private fun rewardPlayer(player: PlayerEntity) {
        // Play XP pickup sound
        player.playSound(
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
            SoundCategory.MASTER,
            1f, 1f
        )

        // Give XP to player
        player.addExperience(rewards.sumOf {
            it.rarity.ordinal + 1
        })

        for (reward in rewards) {
            reward.giveReward(player)
        }
    }

    fun tryCashIn(player: PlayerEntity, stack: ItemStack): Boolean {

        if (timeLeft(player.world) <= 0) {
            player.sendMessage(TranslatableText("bountiful.bounty.expired"), false)
            return false
        }

        return if (hasFinishedObjectives(player)) {
            rewardPlayer(player)
            stack.decrement(stack.maxCount)
            true
        } else {
            player.sendMessage(TranslatableText("bountiful.tooltip.requirements"), false)
            false
        }

    }

    fun verifyValidity(player: PlayerEntity): Boolean {
        val objs = objectives.mapNotNull { it.verifyValidity(player)?.formatted(Formatting.RED) }
        val rews = rewards.mapNotNull { it.verifyValidity(player)?.formatted(Formatting.RED) }
        val combined = objs + rews
        combined.forEach { text ->
            player.sendMessage(text, false)
        }
        return combined.isEmpty()
    }

    override fun toString(): String {
        return JsonFormats.DataPack.encodeToString(ser, this)
    }

    // ### Formatting ### //

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeft(world) / 20)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun tooltipInfo(world: World): List<Text> {
        return buildList {
            add(TranslatableText("bountiful.tooltip.required").formatted(Formatting.GOLD).append(":"))
            addAll(objectives.map {
                it.textSummary(this@BountyData, MinecraftClient.getInstance().player!!, true)
            })
            add(TranslatableText("bountiful.tooltip.rewards").formatted(Formatting.GOLD).append(":"))
            addAll(rewards.map {
                it.textSummary(this@BountyData, MinecraftClient.getInstance().player!!, false)
            })
        }
    }

    companion object : ItemDataJson<BountyData>() {
        override val identifier = Bountiful.id("bounty_data")
        override val ser = BountyData.serializer()
        override val default: () -> BountyData = { BountyData() }
    }

}