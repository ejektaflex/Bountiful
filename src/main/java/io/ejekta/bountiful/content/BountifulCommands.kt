package io.ejekta.bountiful.content

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.context.CommandContext
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.messages.ClipboardCopy
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.text.sendMessage
import io.ejekta.kambrik.text.textLiteral
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.IdentifierArgumentType.getIdentifier
import net.minecraft.command.argument.NumberRangeArgumentType
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.network.MessageType
import net.minecraft.predicate.NumberRange
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.File


object BountifulCommands : CommandRegistrationCallback {

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean) {

        Bountiful.LOGGER.info("Adding serverside commands..")

        dispatcher.addCommand("bo") {
            requiresCreativeOrOp(2)

            val pools = suggestionListTooltipped {
                BountifulContent.Pools.map { pool ->
                    var trans = pool.usedInDecrees.map { it.translation }
                    val translation = if (trans.isEmpty()) {
                        LiteralText("None")
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
                    argString("decType", items = decrees) runs { decType ->
                        val stack = DecreeItem.create(decType())
                        source.player.giveItemStack(stack)
                    }
                }

                "bounty" {
                    argInt("rep", -30..30) runs { rep ->
                        genBounty(rep()).run(this)
                    }
                }
            }


            // /bo pool [poolName]
            // /bo pool [poolName] add hand
            // /bo pool [poolName] add hand (minAmt)..(maxAmt) (unitWorth)
            // /bo pool [poolName] add tag [#tag] (not yet implemented)
            // /bo pool [poolName] add entity (entity_id)
            // /bo pool [poolName] add entity (entity_id) (minAmt)..(maxAmt) (unitWorth)
            "pool" {

                argString("poolName", items = pools) { poolName ->
                    "add" {
                        "hand" {
                            this runs { addHandToPool(poolName = poolName()).run(this) }
                            argIntRange("amount") { amount ->
                                argInt("unit_worth") runs { worth ->
                                    addToPoolCommand(amount(), worth()) { amtRange, worthAmt ->
                                        addHandToPool(amtRange, worthAmt, poolName()).run(this)
                                    }.run(this)
                                }
                            }
                        }

                        "entity" {
                            val entityTypes = suggestionList { Registry.ENTITY_TYPE.ids.toList() }
                            argIdentifier("entity_identifier", items = entityTypes) { eId ->
                                this runs {
                                    addEntityToPool(null, null, eId(), poolName()).run(this)
                                }
                                argIntRange("amount") { amount ->
                                    argInt("unit_worth") runs { worth ->
                                        addToPoolCommand(amount(), worth()) { amtRange, worthAmt ->
                                            addEntityToPool(amtRange, worthAmt, eId(), poolName()).run(this)
                                        }.run(this)
                                    }
                                }
                            }

                        }

                    }
                }

            }

            "util" {
                "debug" {
                    "weights" {
                        argInt("rep", -30..30) runs { rep ->
                            weights(rep()).run(this)
                        }
                    }
                    "dump" runs dumpData()
                }

                "verify" {
                    "pools" runs verifyPools()
                    "hand" runs verifyBounty()
                }
            }

        }
    }

    private fun hand() = kambrikCommand<ServerCommandSource> {
        val held = source.player.mainHandStack

        val newPoolEntry = PoolEntry.create().apply {
            content = held.identifier.toString()
            nbt = if (source.player.mainHandStack == ItemStack.EMPTY) null else held.nbt
        }

        try {
            val saved = newPoolEntry.save(JsonFormats.Hand)
            source.player.sendMessage(saved)
            ClipboardCopy(saved).sendToClient(source.player)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToPoolCommand(
        amt: NumberRange.IntRange,
        inWorth: Int,
        func: (amount: IntRange, worth: Int) -> Unit = { a, w -> }
    ) = kambrikCommand<ServerCommandSource> {
        if (amt.min == null || amt.max == null) {
            source.player.sendMessage("Amount Range must have a minimum and maximum value!")
            return@kambrikCommand
        }

        func(amt.min!!..amt.max!!, inWorth)
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
                edit { content.add(newPoolEntry) }
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

    private fun addHandToPool(inAmount: IntRange? = null, inUnitWorth: Int? = null, poolName: String) = kambrikCommand<ServerCommandSource> {
        val held = source.player.mainHandStack

        addToPool(source.player, inAmount, inUnitWorth, poolName) {
            content = held.identifier.toString()
            nbt = held.nbt
        }
    }

    private fun addEntityToPool(
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        entityId: Identifier,
        poolName: String
    ) = kambrikCommand<ServerCommandSource> {
        try {
            addToPool(source.player, inAmount, inUnitWorth, poolName) {
                type = BountyType.ENTITY
                content = entityId.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun complete() = kambrikCommand<ServerCommandSource> {
        val held = source.player.mainHandStack
        val data = BountyData[held]
        try {
            data.tryCashIn(source.player, held)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun genBounty(rep: Int) = kambrikCommand<ServerCommandSource> {
        try {
            val bd = BountyCreator.create(BountifulContent.Decrees.toSet(), rep, source.player.world.time)
            source.player.giveItemStack(BountyItem.create(bd))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun weights(rep: Int) = kambrikCommand<ServerCommandSource> {
        try {

            println("RARITY WEIGHTS:")
            BountyRarity.values().forEach { rarity ->
                println("${rarity.name}\t ${rarity.weightAdjustedFor(rep)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun verifyBounty() = kambrikCommand<ServerCommandSource> {
        val held = source.player.mainHandStack
        if (held.item is BountyItem) {
            if (!BountyData[held].verifyValidity(source.player)) {
                source.player.sendMessage("Please report this to the modpack author (or the mod author, if this is not part of a modpack)") {
                    format(Formatting.DARK_RED, Formatting.BOLD)
                }
            }
        }
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
            for (item in pool.content.sortedBy { it.content }) {
                Bountiful.LOGGER.info("    * [${item.type}] ${item.content}")
            }
        }
    }

    private fun verifyPools() = kambrikCommand<ServerCommandSource> {
        var errors = false

        for (pool in BountifulContent.Pools) {
            for (poolEntry in pool) {
                val dummy = BountyData()
                val data = poolEntry.toEntry()
                data.let {
                    when {
                        it.type.isObj -> dummy.objectives.add(it)
                        it.type.isReward -> dummy.rewards.add(it)
                        else -> throw Exception("Pool Data was neither an entry nor a reward!: '${poolEntry.type.name}', '${poolEntry.content}'")
                    }
                }

                if (!dummy.verifyValidity(source.player)) {
                    source.player.sendMessage("    - Source Pool: '${pool.id}'", Formatting.RED, Formatting.ITALIC)
                    errors = true
                }
            }
        }

        if (errors) {
            source.player.sendMessage("Some items are invalid. See above for details", Formatting.DARK_RED, Formatting.BOLD)
        } else {
            source.player.sendMessage("All Pool data has been verified successfully.", Formatting.BOLD)
        }
    }

}