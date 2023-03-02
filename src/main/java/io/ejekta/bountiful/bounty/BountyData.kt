package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.types.IBountyObjective
import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.kambrik.serial.ItemDataJson
import kotlinx.serialization.Serializable
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Suppress("RemoveRedundantQualifierName")
@Serializable
class BountyData {

    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()

    private fun hasFinishedObjectives(player: PlayerEntity): Boolean {
        return objectives.all {
            (it.logic as IBountyObjective).tryFinishObjective(it, player)
        }
    }

    val objectiveTypes: List<IBountyObjective>
        get() = objectives.map { it.logic as IBountyObjective }

    private fun rewardPlayer(player: PlayerEntity) {
        // Play XP pickup sound
        player.playSound(
            SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
            SoundCategory.MASTER,
            1f, 1f
        )

        // Give XP to player
        player.addExperience(rewards.sumOf {
            (it.rarity.ordinal) * 2 + 1
        })

        for (reward in rewards) {
            (reward.logic as IBountyReward).giveReward(reward, player)
        }
    }

    fun tryCashIn(player: PlayerEntity, stack: ItemStack): Boolean {

        if (BountyInfo[stack].timeLeft(player.world) <= 0) {
            player.sendMessage(Text.translatable("bountiful.bounty.expired"))
            return false
        }

        return if (hasFinishedObjectives(player)) {
            rewardPlayer(player)
            stack.decrement(stack.maxCount)
            true
        } else {
            player.sendMessage(Text.translatable("bountiful.tooltip.requirements"), false)
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

    companion object : ItemDataJson<BountyData>() {
        override val identifier = Bountiful.id("bounty_data")
        override val ser = BountyData.serializer()
        override val default: () -> BountyData = { BountyData() }
    }

}