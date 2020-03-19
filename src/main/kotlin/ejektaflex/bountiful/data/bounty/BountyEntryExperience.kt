package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.logic.BountyProgress
import ejektaflex.bountiful.logic.IBountyObjective
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.item.ExperienceOrbEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent

class BountyEntryExperience : BountyEntry(), IBountyObjective, IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Experience.id

    override val calculatedWorth: Int
        get() = unitWorth * amount

    override val formattedName: ITextComponent
        get() = when (content) {
            "levels" -> TranslationTextComponent("bountiful.bounty.type.experience.levels")
            "points" -> TranslationTextComponent("bountiful.bounty.type.experience.points")
            else -> StringTextComponent("??? (xp)")
        }

    override fun tooltipReward(): ITextComponent {
        return StringTextComponent(amount.toString() + "x ").applyTextStyle {
            it.color = TextFormatting.WHITE
        }.appendSibling(
                formattedName.applyTextStyle {
                    it.color = TextFormatting.AQUA
                }
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

    override fun tooltipObjective(progress: BountyProgress): ITextComponent {
        return StringTextComponent(progress.stringNums).applyTextStyle {
            it.color = progress.color
        }.appendSibling(
                formattedName
        )
    }

}