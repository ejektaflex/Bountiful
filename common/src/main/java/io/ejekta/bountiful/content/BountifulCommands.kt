package io.ejekta.bountiful.content

import com.google.common.collect.ImmutableList
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.messages.ClipboardCopy
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.decree.DecreeItem
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.util.checkOnBoard
import io.ejekta.bountiful.util.ensureMemoryModules
import io.ejekta.bountiful.util.hackySetTaskTo
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.addCommand
import io.ejekta.kambrik.command.kambrikCommand
import io.ejekta.kambrik.command.requiresOp
import io.ejekta.kambrik.command.suggestionListTooltipped
import io.ejekta.kambrik.command.types.PlayerCommand
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.ext.math.floor
import io.ejekta.kambrik.ext.math.toVec3d
import io.ejekta.kambrik.text.sendMessage
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.entity.ai.TargetPredicate
import net.minecraft.entity.ai.brain.Activity
import net.minecraft.entity.ai.brain.MemoryModuleType
import net.minecraft.entity.ai.brain.Schedule
import net.minecraft.entity.ai.brain.WalkTarget
import net.minecraft.entity.ai.brain.task.GoToIfNearbyTask
import net.minecraft.entity.ai.brain.task.SingleTickTask
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.item.ItemStack
import net.minecraft.predicate.NumberRange
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.GlobalPos
import net.minecraft.world.poi.PointOfInterestStorage
import net.minecraft.world.poi.PointOfInterestType
import java.util.*
import kotlin.jvm.optionals.getOrNull


object BountifulCommands {

    fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registryAccess: CommandRegistryAccess,
        environment: CommandManager.RegistrationEnvironment?
    ) {
        Bountiful.LOGGER.info("Adding serverside commands..")

        dispatcher.addCommand("bo") {

            requiresOp(2)

            val pools = suggestionListTooltipped {
                BountifulContent.Pools.map { pool ->
                    var trans = pool.usedInDecrees.map { it.translation }
                    val translation = if (trans.isEmpty()) {
                        Text.literal("None")
                    } else {
                        trans.reduce { acc, decree ->
                            acc.append(", ").append(decree)
                        }
                    }
                    pool.id to translation
                }
            }

            val decrees = suggestionListTooltipped {
                BountifulContent.Decrees.map { decree ->
                    decree.id to decree.translation
                }
            }

            // /bo hand
            // /bo hand complete
            "hand" {
                this runs hand()
                "complete" runs complete()
            }

            // /bo gen decree (decType)
            // /bo gen bounty (rep_level)
            "gen" {
                "decree" {
                    "type" {
                        argString("decType", items = decrees) runs { decType ->
                            val stack = DecreeItem.create(listOf(decType()))
                            source.player?.giveItemStack(stack)
                        }
                    }
                    "rank" {
                        argInt("rank", 1..5) runs { rank ->
                            val stack = DecreeItem.create(DecreeSpawnCondition.NONE, ranked = rank())
                            source.player?.giveItemStack(stack)
                        }
                    }
                }

                "bounty" {
                    argInt("rep", -30..30) runs { rep ->
                        genBounty(rep()).run(this)
                    }
                }
            }

            "util" {
                "debug" {
                    "weights" {
                        argInt("rep", -30..30) runs { rep ->
                            weights(rep())
                        }
                    }
                    "dump" runs dumpData()
                }
            }

            "config" {
                "reload" runs {
                    BountifulIO.reloadConfig()
                    source.sendMessage(Text.literal("Bountiful Config Reloaded!"))
                }
            }

            "vill" runs {
                doThing(this)
            }

        }
    }

    private fun doThing(ctx: CommandContext<ServerCommandSource>) {
        ctx.run {
            val player = source.playerOrThrow
            val villager = player.world.getClosestEntity(
                VillagerEntity::class.java,
                TargetPredicate.DEFAULT,
                player,
                player.x,
                player.y,
                player.z,
                Box.of(player.pos, 100.0, 100.0, 100.0)
            )
            if (villager != null) {
                source.sendMessage(Text.literal("Found villager at: ${villager.pos} - ${villager.pos.distanceTo(player.pos)}"))

                //player.serverWorld.pointOfInterestStorage.add()

                val serverWorld = player.serverWorld

                val rep: (RegistryEntry<PointOfInterestType>) -> Boolean = { registryEntry ->
                    registryEntry.matchesKey(BountifulContent.POI_BOUNTY_BOARD)
                }

                val nearestBB = serverWorld.pointOfInterestStorage.getNearestPosition(
                    rep, player.blockPos, 32, PointOfInterestStorage.OccupationStatus.ANY
                ).getOrNull()

                if (nearestBB != null) {
                    source.sendMessage(Text.literal("Found BB at: $nearestBB - ${nearestBB.toVec3d().distanceTo(player.pos)}"))
                }

                val brain = villager.brain

                val har = brain.hasActivity(BountifulContent.ACT_CHECK_BOARD)
                source.sendMessage(Text.literal("Har Pre?: $har"))

                val actTime = brain.schedule.getActivityForTime((serverWorld.time % 24000L).toInt())

                source.sendMessage(Text.literal("Currently doing: ${actTime.id}"))

                println(brain)

                nearestBB?.let {
                    villager.checkOnBoard(it)
                }

                for (task in brain.runningTasks) {
                    println("${task.name} - ${task.status}")
                }

            } else {
                source.sendMessage(Text.literal("Villager was null!"))
            }
        }
    }

    private fun hand() = PlayerCommand {
        val held = it.mainHandStack

        val newPoolEntry = PoolEntry.create().apply {
            content = held.identifier.toString()
            nbt = if (it.mainHandStack == ItemStack.EMPTY) null else held.nbt
        }

        try {
            val saved = newPoolEntry.save(JsonFormats.Hand)
            it.let {
                it.sendMessage(saved)
                ClipboardCopy(saved).sendToClient(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        1
    }

    private fun CommandContext<ServerCommandSource>.addToPoolCommand(
        amt: NumberRange.IntRange,
        inWorth: Int,
        func: (amount: IntRange, worth: Int) -> Unit = { a, w -> }
    ) {
        val cmd = kambrikCommand<ServerCommandSource> {
            if (amt.min.getOrNull() == null || amt.max.getOrNull() == null) {
                source.sendError(Text.literal("Amount Range must have a minimum and maximum value!"))
                return@kambrikCommand
            }

            func(amt.min.get()..amt.max.get(), inWorth)
        }
        cmd.run(this)
    }

    private fun addToPool(
        player: ServerPlayerEntity,
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        poolName: String,
        poolFunc: PoolEntry.() -> Unit
    ) {
        val newPoolEntry = PoolEntry.create().apply {
            if (inAmount != null) {
                amount = PoolEntry.EntryRange(inAmount.first, inAmount.last)
            }
            if (inUnitWorth != null) {
                unitWorth = inUnitWorth.toDouble()
            }
        }.apply(poolFunc)

        if (poolName.trim() != "") {

            val file = BountifulIO.getPoolFile(poolName).apply {
                ensureExistence()
                edit { items.add(newPoolEntry) }
            }.getOrCreateFile()

            player.sendMessage("Content added.")
            player.sendMessage("Edit §6'config/bountiful/bounty_pools/$poolName.json'§r to edit details.") {
                clickEvent = ClickEvent(ClickEvent.Action.OPEN_FILE, file.absolutePath)
                onHoverShowText { addLiteral("Click to open file '${file.name}'") }
            }
        } else {
            player.sendMessage("Invalid pool name!")
        }

    }

    private fun CommandContext<ServerCommandSource>.addHandToPool(inAmount: IntRange? = null, inUnitWorth: Int? = null, poolName: String) {
        val cmd = PlayerCommand {
            val held = it.mainHandStack

            addToPool(it, inAmount, inUnitWorth, poolName) {
                content = held.identifier.toString()
                nbt = held.nbt
            }
            1
        }
        cmd.run(this)
    }

    private fun CommandContext<ServerCommandSource>.addEntityToPool(
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        entityId: Identifier,
        poolName: String
    ) {
        val cmd = kambrikCommand<ServerCommandSource> {
            try {
                addToPool(source.player!!, inAmount, inUnitWorth, poolName) {
                    type = BountyTypeRegistry.ENTITY.id
                    content = entityId.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cmd.run(this)
    }

    private fun complete() = PlayerCommand {
        val held = it.mainHandStack
        val data = BountyData[held]
        try {
            data.tryCashIn(it, held)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun genBounty(rep: Int) = PlayerCommand {
        try {
            val sourcePos = BlockPos(source.position.floor())
            val stack = BountyCreator.createBountyItem(
                source.world,
                sourcePos,
                BountifulContent.Decrees.toSet(),
                rep,
                it.world.time
            )
            it.giveItemStack(stack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun CommandContext<ServerCommandSource>.weights(rep: Int) {
        val cmd = kambrikCommand<ServerCommandSource> {
            try {

                println("RARITY WEIGHTS:")
                BountyRarity.values().forEach { rarity ->
                    println("${rarity.name}\t ${rarity.weightAdjustedFor(rep)}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cmd.run(this)
    }

    private fun dumpData() = kambrikCommand<ServerCommandSource> {
        for (decree in BountifulContent.Decrees.sortedBy { it.id }) {
            Bountiful.LOGGER.info("Decree: ${decree.id}")
            for (obj in decree.objectives.sortedBy { it }) {
                Bountiful.LOGGER.info("    * [OBJ] $obj")
            }
            for (rew in decree.rewards.sortedBy { it }) {
                Bountiful.LOGGER.info("    * [REW] $rew")
            }
        }

        for (pool in BountifulContent.Pools.sortedBy { it.id }) {
            Bountiful.LOGGER.info("Pool: ${pool.id}")
            for (item in pool.items.sortedBy { it.content }) {
                Bountiful.LOGGER.info("    * [${item.type.path}] - ${item.id} - ${item.weightMult} - ${item.content}")
            }
        }
    }


}