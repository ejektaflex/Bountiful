package io.ejekta.bountiful.common.content

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.bounty.BountyData
import io.ejekta.bountiful.common.bounty.BountyDataEntry
import io.ejekta.bountiful.common.bounty.BountyRarity
import io.ejekta.bountiful.common.bounty.BountyType
import io.ejekta.bountiful.common.config.*
import io.ejekta.kambrik.ext.id
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.MessageType
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.LiteralText
import java.nio.file.Path
import java.nio.file.Paths


object BountifulCommands {

    fun hasPermission(c: ServerCommandSource): Boolean {
        if (c.hasPermissionLevel(2) ||
            (c.entity is PlayerEntity && c.player.isCreative)) {
            return true
        }
        return false
    }

    fun registerCommands() = CommandRegistrationCallback { dispatcher, dedicated ->
        dispatcher.register(

            /*

            requires(::hasPermission)

            literal("tweak") { does(tweak()) }
            literal("hand") { does(hand()) }
            ...
            literal("gen") {
                argInt("rep") {
                    does(gen())
                }
            }

             */


            CommandManager.literal("bo")
                .requires(::hasPermission)
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
                    CommandManager.literal("pool")
                        .then(
                            CommandManager.literal("addto")
                                .then(
                                    CommandManager.argument("poolName", string())
                                        .suggests { _, builder ->
                                            BountifulContent.Pools.forEach { pool ->
                                                builder.suggest(pool.id)
                                            }
                                            builder.buildFuture()
                                        }
                                        .executes(addHandToPool())
                                )
                        )
                        .then(
                            CommandManager.literal("create")
                                .then(
                                    CommandManager.argument("poolName", string())
                                        .suggests { _, builder ->
                                            BountifulContent.Pools.forEach { pool ->
                                                builder.suggest(pool.id)
                                            }
                                            builder.buildFuture()
                                        }
                                        .executes(addPool())
                                )
                        )
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
                .then(
                    CommandManager.literal("decree")
                        .then(
                            CommandManager.argument("decType", string())
                                .suggests { context, builder ->
                                    BountifulContent.Decrees.forEach { dec ->
                                        builder.suggest(dec.id)
                                    }
                                    builder.buildFuture()
                                }
                                .executes {
                                    val decId = getString(it, "decType")
                                    val stack = DecreeItem.create(decId)
                                    it.source.player.giveItemStack(stack)
                                    1
                                }
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

    private fun tweak() = Command<ServerCommandSource> { ctx ->

        val player = ctx.source.entity as? ServerPlayerEntity ?: return@Command 0
        val held = player.mainHandStack

        if (held.item is BountyItem) {
            player.sendMessage(LiteralText("Hello!"), MessageType.CHAT, player.uuid)

            BountyData.edit(held) {
                timeToComplete += 1000
                rarity = BountyRarity.values()[(rarity.ordinal + 1) % BountyRarity.values().size]
            }


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

            val bd = BountyCreator.create(BountifulContent.Decrees.toSet(), rep)

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
            BountifulContent.Decrees.add(
                Decree(
                "farmer",
                mutableSetOf("farmer_objs"),
                mutableSetOf("farmer_rews")
            )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

}