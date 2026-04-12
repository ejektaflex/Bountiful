package io.ejekta.bountiful.content

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.messages.ClipboardCopy
import io.ejekta.bountiful.util.asComponentJson
import io.ejekta.bountiful.util.checkOnBoard
import io.ejekta.bountiful.util.openSimpleMenu
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.command.types.PlayerCommand
import io.ejekta.kambrik.ext.id
import io.ejekta.kambrik.ext.math.floor
import io.ejekta.kambrik.ext.math.toVec3
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.npc.villager.Villager
import net.minecraft.world.entity.schedule.Activity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrNull

object BountifulCommands {

    private fun tr(path: String, vararg args: Any): MutableComponent = Component.translatable("bountiful.command.$path", *args)

    fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registryAccess: CommandBuildContext,
        environment: Commands.CommandSelection?
    ) {
        Bountiful.LOGGER.info("Adding serverside commands..")

        dispatcher.addCommand("bo") {

            requiresOp(2)

            val pools = suggestionListTooltipped {
                BountifulContent.Pools.map { pool ->
                    val trans = pool.usedInDecrees.map { it.translation }
                    val translation = if (trans.isEmpty()) {
                        tr("none")
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

            val poolEntrySuggestions = suggestionList {
                BountifulContent.Pools.map {
                    it.items.map { pe -> pe.id }
                }.flatten().sorted()
            }

            "hand" {
                this runs hand()
                "complete" runs complete()
            }

            "gen" {
                "decree" {
                    "type" {
                        argString("decType", items = decrees) runs { decType ->
                            val stack = DecreeItem.create(listOf(decType()))
                            source.player?.addItem(stack)
                        }
                    }
                    "rank" {
                        argInt("rank", 1..5) runs { rank ->
                            val stack = DecreeItem.create(DecreeSpawnCondition.NONE, ranked = rank())
                            source.player?.addItem(stack)
                        }
                    }
                    "withall" runs {
                        val stack = DecreeItem.createWithAllDecrees()
                        source.player?.addItem(stack)
                    }
                }

                "bounty" {
                    argInt("rep", -30..1024) runs { rep ->
                        genBounty(rep()).run(this)
                    }
                }
            }

            "util" {
                "settings" {
                    "reload" runs {
                        BountifulIO.loadConfig()
                        source.sendSystemMessage(tr("settings_reloaded"))
                    }
                }

                "analyzer" runs {
                    try {
                        source.player?.openSimpleMenu(tr("analyzer.title")) { syncId: Int, playerInventory: Inventory, player: Player ->
                            AnalyzerScreenHandler(syncId, playerInventory, SimpleContainer(AnalyzerScreenHandler.SIZE))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                "packmode" runs {
                    Bountiful.packMode = true
                    source.sendSystemMessage(tr("packmode_enabled"))
                }

                "debug" {
                    "weights" {
                        argInt("rep", -30..1024) runs { rep ->
                            weights(rep())
                        }
                    }

                    "dump" runs dumpData()

                    @Suppress("KotlinConstantConditions")
                    if (Bountiful.nightly) {
                        "dev" {
                            "vill" runs {
                                sendNearestVillagerToABoard(this)
                            }

                            "hold" runs {
                                holdThing(this)
                            }
                        }
                    }

                }

                "check" {
                    "entry" {
                        argString("checkName", items = poolEntrySuggestions) runs { checkName ->
                            checkForEntry(checkName())
                        }
                    }
                }

                "configToDataPack" {
                    argString("packFileName") { resName ->
                        argString("packDescInQuotes") runs { resDesc ->
                            exportToPack(resName(), resDesc())
                        }
                    }
                }
            }

        }
    }

    private fun CommandContext<CommandSourceStack>.exportToPack(named: String, described: String) {
        try {
            BountifulIO.exportDataPack(named, described)
            source.sendSystemMessage(tr("export.success").withStyle(ChatFormatting.GREEN))
        } catch (e: Exception) {
            e.printStackTrace()
            source.sendSystemMessage(tr("export.failed"))
        }
    }

    private fun CommandContext<CommandSourceStack>.checkForEntry(named: String) {
        val found = BountifulContent.Pools.map {
            it.items
        }.flatten().find {
            it.id == named
        }
        if (found != null) {
            source.sendSystemMessage(tr("check_entry.found", named).withStyle(ChatFormatting.GREEN))

            source.sendSystemMessage(
                tr("check_entry.pools").append(
                    Component.literal("${found.protoPool?.id}").withStyle(ChatFormatting.GOLD)
                )
            )

            val decs = found.protoPool?.usedInDecrees?.map { it.id }?.sorted() ?: emptyList()

            source.sendSystemMessage(
                tr("check_entry.decrees").append(
                    Component.literal("$decs").withStyle(ChatFormatting.GOLD)
                )
            )

            if (!found.isValid(source.server)) {
                source.sendFailure(tr("check_entry.invalid", found.id))
            }
        } else {
            source.sendFailure(tr("check_entry.not_found"))
        }
    }

    private fun holdThing(ctx: CommandContext<CommandSourceStack>) {
        ctx.run {
            val player = source.playerOrException
            val villager = player.level().getNearestEntity(
                Villager::class.java,
                TargetingConditions.DEFAULT,
                player,
                player.x,
                player.y,
                player.z,
                AABB.ofSize(player.position(), 100.0, 100.0, 100.0)
            )

            if (villager != null) {
                val thing = ItemStack(Items.CLAY)
                villager.setItemSlot(EquipmentSlot.MAINHAND, thing)
            }
        }
    }

    private fun sendNearestVillagerToABoard(ctx: CommandContext<CommandSourceStack>) {
        ctx.run {
            val player = source.playerOrException
            val villager = player.level().getNearestEntity(
                Villager::class.java,
                TargetingConditions.DEFAULT,
                player,
                player.x,
                player.y,
                player.z,
                AABB.ofSize(player.position(), 100.0, 100.0, 100.0)
            )
            if (villager != null) {
                source.sendSystemMessage(
                    tr("debug.found_villager", villager.position(), villager.position().distanceTo(player.position()))
                )

                val serverWorld = player.level()

                val rep: (Holder<PoiType>) -> Boolean = {
                    false
                }

                val nearestBB = serverWorld.poiManager.findClosest(
                    rep, player.blockPosition(), 32, PoiManager.Occupancy.ANY
                ).getOrNull()

                if (nearestBB != null) {
                    source.sendSystemMessage(
                        tr("debug.found_board", nearestBB, Vec3.atCenterOf(nearestBB).distanceTo(player.position()))
                    )
                }

                val brain = villager.brain
                val actTime = brain.getActiveNonCoreActivity().orElse(Activity.IDLE)

                source.sendSystemMessage(tr("debug.current_activity", actTime.name))

                println(brain)

                nearestBB?.let {
                    villager.checkOnBoard(it)
                }

                for (task in brain.getRunningBehaviors()) {
                    println("${task.debugString()} - ${task.status}")
                }
            } else {
                source.sendSystemMessage(tr("debug.villager_null"))
            }
        }
    }

    private fun hand() = PlayerCommand {
        val held = it.mainHandItem

        val newPoolEntry = PoolEntry.create().apply {
            content = held.item.id.toString()
            components = held.asComponentJson(source.level.registryAccess())
        }

        try {
            val saved = newPoolEntry.save()
            it.sendSystemMessage(Component.literal(saved))
            ClipboardCopy(saved).sendToClient(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun addToPool(
        player: ServerPlayer,
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

            player.sendSystemMessage(tr("add_to_pool.content_added"))
            player.sendSystemMessage(
                tr("add_to_pool.edit_file", "config/bountiful/bounty_pools/$poolName.json").copy().apply {
                    style = style
                        .withClickEvent(ClickEvent.OpenFile(file.absolutePath))
                        .withHoverEvent(HoverEvent.ShowText(tr("add_to_pool.open_file", file.name)))
                }
            )
        } else {
            player.sendSystemMessage(tr("add_to_pool.invalid_pool_name"))
        }
    }

    private fun CommandContext<CommandSourceStack>.addHandToPool(inAmount: IntRange? = null, inUnitWorth: Int? = null, poolName: String) {
        val cmd = PlayerCommand {
            val held = it.mainHandItem

            addToPool(it, inAmount, inUnitWorth, poolName) {
                content = held.id.toString()
            }
            1
        }
        cmd.run(this)
    }

    private fun CommandContext<CommandSourceStack>.addEntityToPool(
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        entityId: Identifier,
        poolName: String
    ) {
        val cmd = kambrikCommand<CommandSourceStack> {
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
        val data = BountyStack(it.mainHandItem)
        try {
            data.tryCashIn(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun genBounty(rep: Int) = PlayerCommand {
        try {
            val sourcePos = BlockPos(source.position.floor())
            val stack = BountyCreator.createBountyItem(
                source.level,
                sourcePos,
                BountifulContent.Decrees.toSet(),
                rep
            )
            it.addItem(stack)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

    private fun CommandContext<CommandSourceStack>.weights(rep: Int) {
        val cmd = kambrikCommand<CommandSourceStack> {
            try {
                Bountiful.LOGGER.info("RARITY WEIGHTS:")
                Bountiful.LOGGER.info("===============")
                BountyRarity.entries.forEach { rarity ->
                    Bountiful.LOGGER.info("${rarity.name}\t ${rarity.weightAdjustedFor(rep)}")
                }
                Bountiful.LOGGER.info("Done.")
                Bountiful.LOGGER.info("DISCOUNT %: ${(1 - BountyCreator.getDiscount(rep)) * 100}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cmd.run(this)
    }

    private fun dumpData() = kambrikCommand<CommandSourceStack> {
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

        source.sendSystemMessage(tr("dumped_to_log"))
    }
}
