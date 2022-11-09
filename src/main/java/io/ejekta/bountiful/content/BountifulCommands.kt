package io.ejekta.bountiful.content

import com.mojang.brigadier.CommandDispatcher
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyData
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.BountyType
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.messages.ClipboardCopy
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.command.types.PlayerCommand
import io.ejekta.kambrik.ext.identifier
import io.ejekta.kambrik.ext.math.toBlockPos
import io.ejekta.kambrik.text.sendMessage
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.item.ItemStack
import net.minecraft.predicate.NumberRange
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.*
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry


object BountifulCommands : CommandRegistrationCallback {

    override fun register(
        dispatcher: CommandDispatcher<ServerCommandSource>,
        registryAccess: CommandRegistryAccess,
        environment: CommandManager.RegistrationEnvironment?
    ) {
        Bountiful.LOGGER.info("Adding serverside commands..")

        dispatcher.addCommand("bo") {
            requiresCreativeOrOp(2)

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
                    argString("decType", items = decrees) runs { decType ->
                        val stack = DecreeItem.create(decType())
                        source.player?.giveItemStack(stack)
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

    private fun addToPoolCommand(
        amt: NumberRange.IntRange,
        inWorth: Int,
        func: (amount: IntRange, worth: Int) -> Unit = { a, w -> }
    ) = kambrikCommand<ServerCommandSource> {
        if (amt.min == null || amt.max == null) {
            source.sendError(Text.literal("Amount Range must have a minimum and maximum value!"))
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

    private fun addHandToPool(inAmount: IntRange? = null, inUnitWorth: Int? = null, poolName: String) = PlayerCommand {
        val held = it.mainHandStack

        addToPool(it, inAmount, inUnitWorth, poolName) {
            content = held.identifier.toString()
            nbt = held.nbt
        }
        1
    }

    private fun addEntityToPool(
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        entityId: Identifier,
        poolName: String
    ) = kambrikCommand<ServerCommandSource> {
        try {
            addToPool(source.player!!, inAmount, inUnitWorth, poolName) {
                type = BountyType.ENTITY
                content = entityId.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            val bd = BountyCreator.createData(
                source.world,
                source.position.toBlockPos(),
                BountifulContent.Decrees.toSet(),
                rep,
                it.world.time
            )
            it.giveItemStack(BountyItem.create(bd))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
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

    private fun verifyBounty() = PlayerCommand {
        val held = it.mainHandStack
        if (held.item is BountyItem) {
            if (!BountyData[held].verifyValidity(it)) {
                source.sendError(Text.literal("Please report this to the modpack author (or the mod author, if this is not part of a modpack)"))
            }
        }
        1
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

    private fun verifyPools() = PlayerCommand {
        var errors = false

        for (pool in BountifulContent.Pools) {
            for (poolEntry in pool) {
                val dummy = BountyData()
                val data = poolEntry.toEntry(source.world, source.position.toBlockPos())
                data.let { bde ->
                    when {
                        bde.type.isObj -> dummy.objectives.add(bde)
                        bde.type.isReward -> dummy.rewards.add(bde)
                        else -> throw Exception("Pool Data was neither an entry nor a reward!: '${poolEntry.type.name}', '${poolEntry.content}'")
                    }
                }

                if (!dummy.verifyValidity(it)) {
                    it.sendMessage("    - Source Pool: '${pool.id}'", Formatting.RED, Formatting.ITALIC)
                    errors = true
                }
            }
        }

        if (errors) {
            it.sendMessage("Some items are invalid. See above for details", Formatting.DARK_RED, Formatting.BOLD)
            return@PlayerCommand 0
        } else {
            it.sendMessage("All Pool data has been verified successfully.", Formatting.BOLD)
            return@PlayerCommand 1
        }

    }

}