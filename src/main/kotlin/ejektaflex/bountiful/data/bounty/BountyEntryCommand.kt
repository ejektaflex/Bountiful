package ejektaflex.bountiful.data.bounty

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ejektaflex.bountiful.data.bounty.enums.BountyType
import ejektaflex.bountiful.logic.IBountyReward
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent

class BountyEntryCommand : BountyEntry(), IBountyReward {

    @Expose
    @SerializedName("type")
    override var bType: String = BountyType.Command.id

    override val calculatedWorth: Int = unitWorth

    override val formattedName: ITextComponent
        get() = StringTextComponent(name ?: content)

    override fun tooltipReward(): ITextComponent {
        return StringTextComponent(name ?: content)
    }

    override fun reward(player: PlayerEntity) {
        player.server!!.commandManager.handleCommand(player.server!!.commandSource, content)
    }


}