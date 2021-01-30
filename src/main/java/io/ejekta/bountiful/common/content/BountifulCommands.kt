package io.ejekta.bountiful.common.content

import com.mojang.brigadier.Command
import io.ejekta.bountiful.common.bounty.data.pool.PoolEntry
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyDataEntry
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.bounty.logic.BountyType
import io.ejekta.bountiful.common.util.id
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.item.ItemStack
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
                    CommandManager.literal("tweak").executes(tweak())
                )
                .then(
                    CommandManager.literal("hand").executes(hand())
                )
                .then(
                    CommandManager.literal("testing").executes(testing())
                )
                .then(
                    CommandManager.literal("complete").executes(complete())
                )
        )
    }

    private fun hand() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack


        val newPoolEntry = PoolEntry.create().apply {
            content = held.id.toString()
            nbtData = held.tag
        }

        val saved = newPoolEntry.save()

        println(saved)
        player.sendMessage(LiteralText(saved), MessageType.CHAT, player.uuid)


        1

    }

    private fun tweak() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        if (held.item is BountyItem) {
            player.sendMessage(LiteralText("Hello!"), MessageType.CHAT, player.uuid)

            val j = BountyData[held].save()

            BountyData.edit(held) {
                timeToComplete += 1000
                rarity = BountyRarity.values()[(rarity.ordinal + 1) % BountyRarity.values().size]
            }

            println(j)

            println(held.tag)
        }

        1
    }

    private fun testing() = Command<ServerCommandSource> { ctx ->

        val bd = BountyData().apply {
            timeStarted = 100
            timeToComplete = 300
            rarity = BountyRarity.EPIC
            objectives.add(
                BountyDataEntry(BountyType.ITEM, "minecraft:dirt", 2)
            )
            rewards.add(
                BountyDataEntry(BountyType.ITEM, "minecraft:iron_ingot", 10)
            )
        }

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        player.giveItemStack(BountyItem.create(bd))


        1
    }

    private fun complete() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        val data = BountyData[held]

        try {
            println(player)
            data.cashIn(player)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        1
    }

}