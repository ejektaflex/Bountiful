package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text


object CommandLogic : IEntryLogic {

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText? {
        val server = player.server ?: return textLiteral("Server does not exist!") // oh my
        val parsed = server.commandManager.dispatcher.parse(entry.content, player.commandSource)
        return textLiteral("Cmd Err: ${parsed.reader.read}")
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): Text {
        return getDescription(entry)
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(getDescription(entry))
    }

    override fun getProgress(entry: BountyDataEntry, player: PlayerEntity) = Progress(0, 1)

    override fun tryFinishObjective(entry: BountyDataEntry, player: PlayerEntity) = true

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val server = player.server ?: return false
        val replacedCmd = entry.content
            .replace("%BOUNTY_AMOUNT%", entry.amount.toString())
            .replace("%PLAYER_NAME%", player.entityName)
            .replace("%PLAYER_NAME_RANDOM", server.playerNames.random())
            .replace("%PLAYER_POSITION%", "${player.pos.x} ${player.pos.y} ${player.pos.z}")
        return server.commandManager.execute(server.commandSource, replacedCmd) > 0
    }

}