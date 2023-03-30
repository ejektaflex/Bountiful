package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.kambrik.text.textLiteral
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier


class BountyTypeCommand : IBountyReward {

    override val id: Identifier = Identifier("command")

    override fun verifyValidity(entry: BountyDataEntry, player: PlayerEntity): MutableText {
        val server = player.server ?: return textLiteral("Server does not exist!") // oh my
        val parsed = server.commandManager.dispatcher.parse(entry.content, player.commandSource)
        return textLiteral("Cmd Err: ${parsed.reader.read}")
    }

    override fun textSummary(entry: BountyDataEntry, isObj: Boolean, player: PlayerEntity): MutableText {
        return getDescription(entry)
    }

    override fun textBoard(entry: BountyDataEntry, player: PlayerEntity): List<Text> {
        return listOf(getDescription(entry))
    }

    override fun giveReward(entry: BountyDataEntry, player: PlayerEntity): Boolean {
        val server = player.server ?: return false
        val replacedCmd = entry.content
            .replace("%BOUNTY_AMOUNT%", entry.amount.toString())
            .replace("%PLAYER_NAME%", player.entityName)
            .replace("%PLAYER_NAME_RANDOM", server.playerNames.random())
            .replace("%PLAYER_POSITION%", "${player.pos.x} ${player.pos.y} ${player.pos.z}")
        return server.commandManager.executeWithPrefix(server.commandSource, replacedCmd) > 0
    }

}