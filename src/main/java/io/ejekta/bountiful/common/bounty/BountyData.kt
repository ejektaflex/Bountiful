package io.ejekta.bountiful.common.bounty

import io.ejekta.bountiful.common.serial.Format
import io.ejekta.bountiful.common.util.GameTime
import io.ejekta.bountiful.common.util.JsonStrict.toJson
import io.ejekta.bountiful.common.util.JsonStrict.toTag
import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.LiteralText
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
    var rarity = Rarity.COMMON
    val objectives = mutableListOf<BountyDataEntry>()
    val rewards = mutableListOf<BountyDataEntry>()

    fun timeLeft(world: World): Long {
        return max(timeStarted - world.time + timeToComplete, 0L)
    }

    fun hasExpired(world: World): Boolean {
        return timeLeft(world) <= 0
    }

    fun save() = Format.NBT.encodeToJsonElement(serializer(), this)

    fun formattedTimeLeft(world: World): Text {
        return GameTime.formatTimeExpirable(timeLeft(world) / 20)
    }

    fun tooltipInfo(world: World): List<MutableText> {
        val lines = mutableListOf<MutableText>()

        lines += TranslatableText("bountiful.tooltip.required").formatted(Formatting.GOLD)

        lines += TranslatableText("bountiful.tooltip.rewards").formatted(Formatting.GOLD)


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

        fun set(stack: ItemStack, value: BountyData) {
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