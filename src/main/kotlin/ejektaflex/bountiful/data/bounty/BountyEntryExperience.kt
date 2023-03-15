package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.ext.withSibling
import net.minecraft.entity.item.ExperienceOrbEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.*

class BountyEntryExperience : BountyEntry(), IBountyObjective, IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Experience.id

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override val formattedName: MutableComponent
        get() = when (content) {
            "levels" -> Component.translatable("bountiful.bounty.type.experience.levels")
            "points" -> Component.translatable("bountiful.bounty.type.experience.points")
            else -> Component.literal("??? (xp)")
        }

    override fun tooltipReward(): Component {
        return Component.literal(amount.toString() + "x ").withStyle(ChatFormatting.WHITE).withSibling(
                formattedName.withStyle(ChatFormatting.AQUA)
        )
    }

    private fun dropXpOnPlayer(player: PlayerEntity, amt: Int) {
        var dropsLeft = amt
        while (dropsLeft > 0) {
            val dropAmount = ExperienceOrbEntity.getXPSplit(dropsLeft)
            dropsLeft -= dropAmount
            player.world.addEntity(ExperienceOrbEntity(player.world, player.posX, player.posY, player.posZ, dropAmount))
        }
    }

    override fun reward(player: PlayerEntity) {

        when (content) {
            "levels" -> {
                player.addExperienceLevel(amount)
            }
            "points" -> {
                if (BountifulConfig.SERVER.doXpDrop.get()) {
                    dropXpOnPlayer(player, amount)
                } else {
                    player.giveExperiencePoints(amount)
                }
            }
            else -> BountifulMod.logger.error("Experience reward tried to give '$content', which is invalid. Content must be 'points' or 'levels'")
        }

    }

    override fun tooltipObjective(progress: BountyProgress): Component {
        return Component.literal(progress.stringNums).withStyle(progress.color).withSibling(
            formattedName
        )
    }

}