package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text


class BiomeLogic(override val entry: BountyDataEntry) : IEntryLogic {

    override fun verifyValidity(player: PlayerEntity): MutableText? = null

    override fun textSummary(isObj: Boolean, player: PlayerEntity): Text {
        return description
    }

    private val description: Text
        get() = entry.translation?.let {
            textTranslate(it)
        } ?: entry.name?.let {
            textLiteral(it)
        } ?: textLiteral(entry.content)

    override fun textBoard(player: PlayerEntity): List<Text> {
        return listOf(description)
    }

    override fun getProgress(player: PlayerEntity) = Progress(0, 0)

    override fun tryFinishObjective(player: PlayerEntity) = true

    override fun giveReward(player: PlayerEntity): Boolean {
        val server = player.server ?: return false
        val replacedCmd = entry.content
            .replace("%BOUNTY_AMOUNT%", entry.amount.toString())
            .replace("%PLAYER_NAME%", player.entityName)
            .replace("%PLAYER_POSITION%", "${player.pos.x} ${player.pos.y} ${player.pos.z}")
        return server.commandManager.execute(server.commandSource, replacedCmd) > 0
    }

}