package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountyType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.ChatFormatting
import net.minecraft.world.entity.player.Player

class BountyEntryCommand : BountyEntry(), IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Command.id

    override val calculatedWorth: Int = unitWorth

    override val formattedName: MutableComponent
        get() = Component.literal(name ?: content)

    override fun tooltipReward(): MutableComponent {
        return formattedName.withStyle(ChatFormatting.BOLD)
    }

    override fun reward(player: Player) {
        val server = player.server!!
        var newCommand = content
        newCommand = newCommand.replace("%player%", player.displayName.string) // .formattedText?
        newCommand = newCommand.replace("%amount%", amount.toString())
        BountifulMod.logger.info("About to run command: $newCommand")
        server.commands.performPrefixedCommand(server.createCommandSourceStack(), newCommand)
    }

}