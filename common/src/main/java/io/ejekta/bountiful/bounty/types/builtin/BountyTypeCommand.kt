package io.ejekta.bountiful.bounty.types.builtin

import io.ejekta.bountiful.bounty.types.IBountyReward
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.data.PoolEntry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import kotlin.random.Random


class BountyTypeCommand : IBountyReward {

    override val id: ResourceLocation = ResourceLocation.parse("command")

    override fun isValid(entry: PoolEntry, server: MinecraftServer): Boolean {
        val parsed = server.commands.dispatcher.parse(entry.content, server.createCommandSourceStack())
        return parsed.exceptions.isEmpty()
    }

    override fun textOnBounty(entry: BountyDataEntry, isObj: Boolean, player: Player, current: Int): MutableComponent {
        return getDescription(entry)
    }

    override fun textOnBoardSidebar(entry: BountyDataEntry, player: Player): List<Component> {
        return listOf(getDescription(entry))
    }

    override fun giveReward(entry: BountyDataEntry, player: Player) {
        val server = player.server ?: return
        val replacedCmd = entry.content
            .replace("%BOUNTY_AMOUNT%", entry.amount.toString())
            .replace("%PLAYER_NAME%", player.scoreboardName)
            .replace("%PLAYER_NAME_RANDOM%", server.playerNames.random())
            .replace("%PLAYER_POSITION%", "${player.position().x} ${player.position().y} ${player.position().z}")
            // Should not NPE since capture group would fail first
            .replace(Regex("%RANDOM_INT\\((?<low>-*\\d+),\\s*(?<high>-*\\d+)\\)%")) {
                result -> Random.nextInt(
                    result.groups["low"]!!.value.toInt(),
                    result.groups["high"]!!.value.toInt() + 1 // exclusive until, needs increase by 1
                ).toString()
            }
        server.commands.performPrefixedCommand(server.createCommandSourceStack(), replacedCmd)
    }

}