package io.ejekta.bountiful.common.content

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.StringArgumentType.getString
import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.BountyData
import io.ejekta.bountiful.common.bounty.BountyRarity
import io.ejekta.bountiful.common.config.*
import io.ejekta.kambrik.commands.*
import io.ejekta.kambrik.ext.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.MessageType
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText


object BountifulCommands {

    private fun hasPermission(c: ServerCommandSource): Boolean {
        if (c.hasPermissionLevel(2) ||
            (c.entity is PlayerEntity && c.player.isCreative)) {
            return true
        }
        return false
    }

    fun registerCommands() = CommandRegistrationCallback { dispatcher, dedicated ->
        dispatcher.register(
            kambrikLiteral("bo") {
                requires(::hasPermission)

                "hand" execs hand()
                "complete" execs complete()

                "pool" {
                    "addto" {
                        stringArgExecutes("poolName", addHandToPool(), BountifulContent.Pools.map { it.id })
                    }
                    "create" {
                        stringArgExecutes("poolName", addPool(), BountifulContent.Pools.map { it.id })
                    }
                }

                "gen" { intArgExecutes("rep", gen()) }
                "weights" { intArgExecutes("rep", weights()) }

                "decree" {
                    stringArgExecutes("decType", {
                        val decId = getString(it, "decType")
                        val stack = DecreeItem.create(decId)
                        it.source.player.giveItemStack(stack)
                        1
                    }, BountifulContent.Pools.map { it.id })
                }

            }

        )
    }

    private fun hand() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        val newPoolEntry = PoolEntry.create().apply {
            content = held.id.toString()
            nbtData = if (player.mainHandStack == ItemStack.EMPTY) null else held.tag
        }

        val saved = newPoolEntry.save(Format.Hand)

        println(saved)
        player.sendMessage(LiteralText(saved), MessageType.CHAT, player.uuid)

        val packet = PacketByteBuf(Unpooled.buffer())
        packet.writeString(saved)
        ServerPlayNetworking.send(player, Bountiful.id("copydata"), packet)

        1
    }

    private fun addHandToPool() = Command<ServerCommandSource> { ctx ->
        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack
        val poolName = getString(ctx, "poolName")

        val newPoolEntry = PoolEntry.create().apply {
            content = held.id.toString()
            nbtData = held.tag
        }

        if (poolName.trim() != "") {
            BountifulIO.editPoolConfig(poolName) {
                content.add(newPoolEntry)
            }
            player.sendMessage(LiteralText("Item added to pool '$poolName'."), MessageType.CHAT, player.uuid)
            player.sendMessage(LiteralText("Edit 'config/bountiful/bounty_pools/$poolName.json' to edit details."), MessageType.CHAT, player.uuid)
        } else {
            player.sendMessage(LiteralText("Invalid pool name!"), MessageType.CHAT, player.uuid)
        }

        1
    }

    private fun addPool() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val poolName = getString(ctx, "poolName")

        if (poolName.trim() != "") {
            BountifulIO.getOrCreatePoolConfig(poolName)
            player.sendMessage(LiteralText("Pool '$poolName' created (if it did not exist)"), MessageType.CHAT, player.uuid)
            player.sendMessage(LiteralText("Use '/reload' to see changes."), MessageType.CHAT, player.uuid)
        } else {
            player.sendMessage(LiteralText("Invalid pool name!"), MessageType.CHAT, player.uuid)
        }

        1
    }

    private fun complete() = Command<ServerCommandSource> { ctx ->
        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack
        val data = BountyData[held]

        try {
            println(player)
            data.tryCashIn(player, held)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun gen() = Command<ServerCommandSource> { ctx ->
        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        try {
            val rep = getInteger(ctx, "rep")
            val bd = BountyCreator.create(BountifulContent.Decrees.toSet(), rep)
            player.giveItemStack(BountyItem.create(bd))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun weights() = Command<ServerCommandSource> { ctx ->

        try {
            val rep = getInteger(ctx, "rep")

            println("RARITY WEIGHTS:")
            BountyRarity.values().forEach { rarity ->
                println("${rarity.name}\t ${rarity.weightAdjustedFor(rep)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

}