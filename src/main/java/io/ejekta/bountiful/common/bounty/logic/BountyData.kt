package io.ejekta.bountiful.common.bounty.logic

import io.ejekta.bountiful.common.serial.Format
import io.ejekta.bountiful.common.util.GameTime
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Rarity
import net.minecraft.world.World
import kotlin.math.max

@Serializable
class BountyData {

    var timeStarted = -1L
    var timeToComplete = -1L
    var rarity = BountyRarity.COMMON
    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()

    private fun timeLeft(world: World): Long {
        return max(timeStarted - world.time + timeToComplete, 0L)
    }

    fun save() = Format.NBT.encodeToJsonElement(serializer(), this)

    fun cashIn(player: PlayerEntity) {
        val objs = objectives.map {
            println("${this.save()},,, $it,, $player")
            println(it.type)
            it().finishObjective(this, it, player)
        }

        if (objs.all { it }) {
            rewards.forEach {
                it().giveReward(this, it, player)
            }
        } else {
            player.sendMessage(TranslatableText("bountiful.tooltip.requirements"), false)
            println("All objectives finished but some returned false!")
        }

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



    companion object {

        operator fun get(stack: ItemStack) : BountyData {
            return if (stack.hasTag()) {
                val data = stack.tag!!.toJson()
                return try {
                    Format.NBT.decodeFromJsonElement(serializer(), data)
                } catch (e: Exception) {
                    setSafeData(stack)
                }
            } else {
                setSafeData(stack)
            }
        }

        fun getUnsafe(stack: ItemStack) : BountyData {
            val data = stack.tag!!.toJson()
            return Format.NBT.decodeFromJsonElement(serializer(), data)
        }

        operator fun set(stack: ItemStack, value: BountyData) {
            stack.tag = Format.NBT.encodeToJsonElement(serializer(), value).toTag() as CompoundTag
        }

        fun edit(stack: ItemStack, func: BountyData.() -> Unit) {
            get(stack).apply(func).also { set(stack, it) }
        }

        fun setSafeData(stack: ItemStack): BountyData {
            return BountyData().apply {
                stack.tag = Format.NBT.encodeToJsonElement(serializer(), this).toTag() as CompoundTag
            }
        }

    }

}