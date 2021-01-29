package io.ejekta.bountiful.common.content

import com.mojang.brigadier.Command
import io.ejekta.bountiful.common.bounty.BountyData
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.network.MessageType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import net.minecraft.util.Rarity


object BountifulCommands {

    fun registerCommands() = CommandRegistrationCallback { dispatcher, dedicated ->
        dispatcher.register(
            CommandManager.literal("bo")
                .then(
                    CommandManager.literal("hand").executes(show())
                )
        )
    }

    private fun show() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        if (held.item is BountyItem) {
            player.sendMessage(LiteralText("Hello!"), MessageType.CHAT, player.uuid)

            val j = BountyData[held].save()

            BountyData.edit(held) {
                timeToComplete += 1000
                rarity = Rarity.values()[(rarity.ordinal + 1) % Rarity.values().size]
            }

            println(j)

            println(held.tag)
        }

        1
    }

}