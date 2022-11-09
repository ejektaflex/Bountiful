package io.ejekta.bountiful.bounty

import io.ejekta.bountiful.bounty.logic.IEntryLogic
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos

// Tracks the status of a given bounty
@Serializable
data class BountyDataEntry private constructor(
    val type: BountyType,
    val content: String,
    val amount: Int,
    var nbt: @Contextual NbtCompound? = null,
    var name: String? = null,
    var translation: String? = null,
    var isMystery: Boolean = false,
    var rarity: BountyRarity = BountyRarity.COMMON,
    var tracking: @Contextual NbtCompound = NbtCompound(), // Used to track extra data, e.g. current progress if needed
    var criteria: CriteriaData? = null,
    val current: Int = 0
) {

    val logic: IEntryLogic
        get() = type.logic(this)

    @Transient
    var worth = Double.MIN_VALUE

    override fun toString(): String {
        return "BDE[type=$type, content=$content, amount=$amount, isNbtNull=${nbt == null}, name=$name, mystery=$isMystery]"
    }

    fun textBoard(player: PlayerEntity): List<Text> {
        return logic.textBoard(this, player)
    }

    fun textSummary(data: BountyData, player: PlayerEntity, isObj: Boolean): MutableText {
        return when (isMystery) {
            true -> Text.literal("???").formatted(Formatting.BOLD).append(
                Text.literal("x$amount").formatted(Formatting.WHITE)
            )
            false -> logic.textSummary(this, isObj, player)
        }
    }

    fun giveReward(player: PlayerEntity) {
        logic.giveReward(this, player)
    }

    fun tryFinishObjective(player: PlayerEntity) = logic.tryFinishObjective(this, player)

    fun verifyValidity(player: PlayerEntity) = logic.verifyValidity(this, player)

    companion object {

        val DUMMY = BountyDataEntry(BountyType.NULL, "NULL_ENTRY", 1)

        fun of(
            world: ServerWorld,
            pos: BlockPos,
            type: BountyType,
            content: String,
            amount: Int,
            worth: Double,
            nbt: NbtCompound? = null,
            name: String? = null,
            translation: String? = null,
            isMystery: Boolean = false,
            rarity: BountyRarity = BountyRarity.COMMON,
            tracking: NbtCompound = NbtCompound(),
            criteriaData: CriteriaData? = null
        ): BountyDataEntry {
            return BountyDataEntry(
                type, content, amount, nbt, name, translation, isMystery, rarity, tracking, criteriaData
            ).apply {
                this.worth = worth
                type.logic(this).setup(this, world, pos)
            }
        }

    }

}
