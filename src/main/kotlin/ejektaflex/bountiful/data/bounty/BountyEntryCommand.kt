package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting

class BountyEntryCommand : BountyEntry(), IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Command.id

    override val calculatedWorth: Int = unitWorth

    override val formattedName: ITextComponent
        get() = StringTextComponent(name ?: content)

    override fun tooltipReward(): ITextComponent {
        return formattedName.applyTextStyle(TextFormatting.GOLD)
    }

    override fun reward(player: PlayerEntity) {
        val server = player.server!!
        var newCommand = content
        newCommand = newCommand.replace("%player%", player.displayName.formattedText)
        newCommand = newCommand.replace("%amount%", amount.toString())
        BountifulMod.logger.info("About to run command: $newCommand")
        server.commandManager.handleCommand(server.commandSource, newCommand)
    }

}