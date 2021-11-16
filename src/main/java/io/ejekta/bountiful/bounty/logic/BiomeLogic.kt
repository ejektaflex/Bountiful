package io.ejekta.bountiful.bounty.logic

import io.ejekta.bountiful.bounty.BountyDataEntry
import io.ejekta.kambrik.text.textLiteral
import io.ejekta.kambrik.text.textTranslate
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome


class BiomeLogic(override val entry: BountyDataEntry) : IEntryLogic {

    val biomeRegistry: Registry<Biome>?
        get() = MinecraftClient.getInstance().world?.registryManager?.get(
            Registry.BIOME_KEY
        )

    val biome: Biome?
        get() = biomeRegistry?.get(Identifier(entry.content))

    override fun verifyValidity(player: PlayerEntity): MutableText? = null

    override fun textSummary(isObj: Boolean, player: PlayerEntity): Text {
        return textLiteral(entry.content)
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

    override fun getProgress(player: PlayerEntity): Progress {

        return Progress(
            if (player.world.getBiome(player.blockPos) == biome) 1 else 0,
            1
        )
    }

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