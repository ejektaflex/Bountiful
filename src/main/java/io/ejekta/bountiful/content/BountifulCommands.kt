package io.ejekta.bountiful.content

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.config.*
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.api.command.*
import io.ejekta.kambrik.ext.identifier
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.item.ItemStack
import net.minecraft.network.MessageType
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.*
import net.minecraft.util.Formatting
import java.io.File


object BountifulCommands : CommandRegistrationCallback {

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {
        Kambrik.Command.addCommand("bo", dispatcher) {
            requires(Kambrik.Command::hasBasicCreativePermission)

            "hand" runs hand()
            "complete" runs complete()

            "pool" {

                val pools = suggestionListTooltipped {
                    BountifulContent.Pools.map { pool ->
                        pool.id to LiteralText("Used In: ").append(pool.usedInDecrees.map { it.translation }.reduce { acc, decree ->
                            acc.append(", ").append(decree)
                        })
                    }
                }

                "addhand" { stringArg("poolName", items = pools) runs addHandToPool() }
                "create" { stringArg("poolName", items = pools) runs addPool() }
            }

            "gen" { intArg("rep", -30..30) runs gen() }

            "decree" {
                stringArg("decType") runs playerCommand { player ->
                    val decId = getString("decType")
                    val stack = DecreeItem.create(decId)
                    player.giveItemStack(stack)
                    1
                }
            }

            "verify" runs verify()

            "debug" {
                "held" runs debugHeld()
                "weights" { intArg("rep", -30..30) runs weights() }
            }
        }
    }

    private fun hand() = playerCommand { player ->
        val held = player.mainHandStack

        val newPoolEntry = PoolEntry.create().apply {
            content = held.identifier.toString()
            nbtData = if (player.mainHandStack == ItemStack.EMPTY) null else held.tag
        }

        val saved = newPoolEntry.save(JsonFormats.Hand)

        println(saved)
        player.sendMessage(LiteralText(saved), MessageType.CHAT, player.uuid)

        val packet = PacketByteBuf(Unpooled.buffer())
        packet.writeString(saved)
        ServerPlayNetworking.send(player, Bountiful.id("copydata"), packet)

        1
    }

    private fun MutableText.fileOpenerText(file: File): Text {
        return styled {
            it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_FILE, file.absolutePath))
                .withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, LiteralText("Click to open file '${file.name}'")))
        }
    }

    private fun addHandToPool() = playerCommand { player ->
        val poolName = getString("poolName")
        val held = player.mainHandStack

        val newPoolEntry = PoolEntry.create().apply {
            content = held.identifier.toString()
            nbtData = held.tag
        }

        if (poolName.trim() != "") {

            val file = BountifulIO.getPoolFile(poolName).apply {
                edit { content.add(newPoolEntry) }
            }

            player.sendMessage(LiteralText("Edit §6'config/bountiful/bounty_pools/$poolName.json'§r to edit details.").fileOpenerText(file.getOrCreateFile()), MessageType.CHAT, player.uuid)
        } else {
            player.sendMessage(LiteralText("Invalid pool name!"), MessageType.CHAT, player.uuid)
        }

        1
    }

    private fun addPool() = playerCommand { player ->
        val poolName = getString("poolName")

        if (poolName.trim() != "") {
            BountifulIO.getPoolFile(poolName).ensureExistence()
            player.sendMessage(LiteralText("Pool '$poolName' created (if it did not exist)"), MessageType.CHAT, player.uuid)
            player.sendMessage(LiteralText("Use '/reload' to see changes."), MessageType.CHAT, player.uuid)
        } else {
            player.sendMessage(LiteralText("Invalid pool name!"), MessageType.CHAT, player.uuid)
        }

        1
    }

    private fun complete() = playerCommand { player ->
        val held = player.mainHandStack
        val data = BountyData[held]

        try {
            data.tryCashIn(player, held)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun gen() = playerCommand { player ->
        try {
            val rep = getInt("rep")
            val bd = BountyCreator.create(BountifulContent.Decrees.toSet(), rep, player.world.time)
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

    private fun debugHeld() = playerCommand { player ->

        val held = player.mainHandStack

        if (held.item is BountyItem) {
            if (!BountyData[held].verifyValidity(player)) {
                player.sendMessage(
                    LiteralText("Please report this to the modpack author (or the mod author, if this is not part of a modpack)")
                        .formatted(Formatting.RED),
                    false
                )
            }
        }

        1
    }

    private fun verify() = playerCommand { player ->

        val dummy = BountyData()

        for (pool in BountifulContent.Pools) {
            for (poolEntry in pool) {
                val data = poolEntry.toEntry()
                data.let {
                    when {
                        it.type.isObj -> dummy.objectives.add(it)
                        it.type.isReward -> dummy.rewards.add(it)
                        else -> throw Exception("Pool Data was neither an entry nor a reward!: ${poolEntry.type.name}, ${poolEntry.content}")
                    }
                }
            }
        }

        val result = dummy.verifyValidity(player)

        if (!result) {
            player.sendMessage(LiteralText("Some items are invalid. See above for details").formatted(Formatting.RED), false)
        }

        1
    }



}