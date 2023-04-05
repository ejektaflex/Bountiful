package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.bounty.types.IBountyType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry private constructor(
    val id: String,
    val logicId: @Contextual Identifier,
    val content: String,
    val amount: Int,
    var nbt: @Contextual NbtCompound? = null,
    var name: String? = null,
    var icon: @Contextual Identifier? = null,
    var isMystery: Boolean = false,
    var rarity: BountyRarity = BountyRarity.COMMON,
    var tracking: JsonObject = JsonObject(emptyMap()), // Used to track extra data, e.g. current progress if needed
    var critConditions: JsonObject? = null,
    var current: Int = 0 // Current progress
) {

    val translation: MutableText
        get() = Text.translatable("bountiful.entry.${id}")

    val logic: IBountyType
        get() = BountyTypeRegistry[logicId]!!

    @Transient
    var worth = Double.MIN_VALUE

    override fun toString(): String {
        return "BDE[type=$logic, content=$content, amount=$amount, isNbtNull=${nbt == null}, name=$name, mystery=$isMystery]"
    }

    fun textBoard(player: PlayerEntity): List<Text> {
        return logic.textBoard(this, player)
    }

    fun textSummary(player: PlayerEntity, isObj: Boolean): MutableText {
        return when (isMystery) {
            true -> Text.literal("???").formatted(Formatting.BOLD).append(
                Text.literal("x$amount").formatted(Formatting.WHITE)
            )
            false -> logic.textSummary(this, isObj, player)
        }
    }

    companion object {

        fun of(
            id: String,
            world: ServerWorld,
            pos: BlockPos,
            type: Identifier,
            content: String,
            amount: Int,
            worth: Double,
            nbt: NbtCompound? = null,
            name: String? = null,
            icon: Identifier? = null,
            isMystery: Boolean = false,
            rarity: BountyRarity = BountyRarity.COMMON,
            tracking: JsonObject = JsonObject(emptyMap()),
            critConditions: JsonObject? = null
        ): BountyDataEntry {
            return BountyDataEntry(
                id, type, content, amount, nbt, name, icon, isMystery, rarity, tracking, critConditions
            ).apply {
                this.worth = worth
                logic.setup(this, world, pos)
            }
        }

    }

}
