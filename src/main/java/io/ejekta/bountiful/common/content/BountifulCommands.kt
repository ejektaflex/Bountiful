package io.ejekta.bountiful.common.content

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.data.pool.Decree
import io.ejekta.bountiful.common.bounty.data.pool.Pool
import io.ejekta.bountiful.common.bounty.data.pool.PoolEntry
import io.ejekta.bountiful.common.bounty.logic.BountyData
import io.ejekta.bountiful.common.bounty.logic.BountyDataEntry
import io.ejekta.bountiful.common.bounty.logic.BountyRarity
import io.ejekta.bountiful.common.bounty.logic.BountyType
import io.ejekta.bountiful.common.serial.Format
import io.ejekta.bountiful.common.util.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.MessageType
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import java.nio.file.Path
import java.nio.file.Paths





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
                .then(
                    CommandManager.literal("load").executes(load())
                )
                .then(
                    CommandManager.literal("farm").executes(farm())
                )
                .then(
                    CommandManager.literal("gen")
                        .then(
                            CommandManager.argument("rep", integer())
                                .executes(gen())
                        )
                )
                .then(
                    CommandManager.literal("weights")
                        .then(
                            CommandManager.argument("rep", integer())
                                .executes(weights())
                        )
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

        val saved = newPoolEntry.save(Format.Hand)

        println(saved)
        player.sendMessage(LiteralText(saved), MessageType.CHAT, player.uuid)

        val packet = PacketByteBuf(Unpooled.buffer())
        packet.writeString(saved)
        ServerPlayNetworking.send(player, Bountiful.id("copydata"), packet)


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
            data.tryCashIn(player, held)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        1
    }

    private fun load() = Command<ServerCommandSource> { ctx ->

        try {
            BountifulContent.Pools.clear()
            BountifulContent.Decrees.clear()

            val currentRelativePath: Path = Paths.get("")
            val s: String = currentRelativePath.toAbsolutePath().toString()
            println("Current relative path is: $s")

            val dataPath = currentRelativePath.resolve("../src/main/resources/data/bountiful")

            val decreeName = "farmer"
            val poolName = "farmer"

            val dec = dataPath.resolve("bounty_decrees/$decreeName.json").toFile()
            val decree = Format.Normal.decodeFromString(Decree.serializer(), dec.readText())
            decree.id = decreeName
            println("Dec: $decree")

            BountifulContent.Decrees.add(decree)

            fun doot(path: Path, name: String): Pool {
                val pl = path.resolve("bounty_pools/bountiful/$name.json").toFile()
                println("Loading pool: ${pl.absolutePath}")
                val pool = Format.Normal.decodeFromString(Pool.serializer(), pl.readText())
                pool.id = name
                return pool
            }

            val farmerObj = doot(dataPath, "${poolName}_objs")
            val farmerRew = doot(dataPath, "${poolName}_rews")

            BountifulContent.Pools.add(farmerObj)
            BountifulContent.Pools.add(farmerRew)

            println("Check!")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun gen() = Command<ServerCommandSource> { ctx ->

        try {
            val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0

            val rep = getInteger(ctx, "rep")

            val bd = BountyCreator.createBounty(BountifulContent.Decrees.toSet(), rep)

            player.giveItemStack(BountyItem.create(bd))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun weights() = Command<ServerCommandSource> { ctx ->

        try {
            val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
            val held = player.mainHandStack

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

    private fun farm() = Command<ServerCommandSource> { ctx ->

        try {
            BountifulContent.Decrees.add(Decree(
                "farmer",
                mutableSetOf("farmer_objs"),
                mutableSetOf("farmer_rews")
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

}