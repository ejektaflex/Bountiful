package io.ejekta.bountiful.content

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.JsonOps
import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.config.JsonFormats
import io.ejekta.bountiful.content.gui.AnalyzerScreenHandler
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.decree.DecreeSpawnCondition
import io.ejekta.bountiful.messages.ClipboardCopy
import io.ejekta.bountiful.util.checkOnBoard
import io.ejekta.bountiful.util.openSimpleMenu
import io.ejekta.kambrik.command.*
import io.ejekta.kambrik.command.types.PlayerCommand
import io.ejekta.kambrik.ext.id
import io.ejekta.kambrik.ext.math.floor
import io.ejekta.kambrik.ext.math.toVec3
import io.ejekta.kambrik.text.sendMessage
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.RegistryOps
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import kotlin.jvm.optionals.getOrNull


object BountifulCommands {

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
                        Component.literal("None")
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
                    argInt("rep", -30..30) runs { rep ->
                        genBounty(rep()).run(this)
                    }
                }
            }

            "util" {
                "settings" {
                    "reload" runs {
                        BountifulIO.loadConfig()
                        source.sendSystemMessage(Component.literal("Bountiful Settings Reloaded!"))
                    }
                }

                "analyzer" runs {
                    try {
                        source.playerOrException

                        source.player?.run {
                            openSimpleMenu(Component.literal("Analyzer!")) { syncId: Int, playerInventory: Inventory, player: Player ->
                                AnalyzerScreenHandler(syncId, playerInventory, SimpleContainer(AnalyzerScreenHandler.SIZE))
                            }
                        }

                        //ClientPlayerStatus.Type.OPEN_ANALYZER.sendToClient(source.playerOrThrow)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                "debug" {
                    "weights" {
                        argInt("rep", -30..30) runs { rep ->
                            weights(rep())
                        }
                    }

                    "dump" runs dumpData()

                    "dev" {
                        "vill" runs {
                            sendNearestVillagerToABoard(this)
                        }

                        "hold" runs {
                            holdThing(this)
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
            source.sendSystemMessage(Component.literal("Data pack exported successfully. You can find it in the config folder.")
                .withStyle(ChatFormatting.GREEN)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            source.sendSystemMessage(Component.literal("Data pack creation failed!"))
        }
    }

    private fun CommandContext<CommandSourceStack>.checkForEntry(named: String) {
        val found = BountifulContent.Pools.map {
            it.items
        }.flatten().find {
            it.id == named
        }
        if (found != null) {
            source.sendSystemMessage(Component.literal("Pool Entries with id '$named' Found!").withStyle(
                ChatFormatting.GREEN
            ))

            source.sendSystemMessage(Component.literal("* Exists in these pools: ").append(
                Component.literal("${found.protoPool?.id}").withStyle(ChatFormatting.GOLD))
            )

            val decs = found.protoPool?.usedInDecrees?.map { it.id }?.sorted() ?: emptyList()

            source.sendSystemMessage(Component.literal("* Exists in these decrees: ").append(
                Component.literal("$decs").withStyle(ChatFormatting.GOLD)
            ))


            if (!found.isValid(source.server)) {
                source.sendFailure(
                    Component.literal("* Error: Entry ${found.id} seemingly failed validation for some reason.")
                )
            }

        } else {
            source.sendFailure(Component.literal("Pool Entry Not Found! Does not seem to exist in any pool."))
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
//                villager.setStackInHand(Hand.MAIN_HAND, thing)
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
                source.sendSystemMessage(Component.literal("Found villager at: ${villager.position()} - ${villager.position().distanceTo(player.position())}"))

                //player.serverWorld.pointOfInterestStorage.add()

                val serverWorld = player.serverLevel()

                val rep: (Holder<PoiType>) -> Boolean = { registryEntry ->
                    //registryEntry.matchesKey(BountifulContent.POI_BOUNTY_BOARD)
                    //TODO ? was like this in 1.20.4
                    false
                }

                //serverWorld.poiManager.findClosest()
                val nearestBB = serverWorld.poiManager.findClosest(
                    rep, player.blockPosition(), 32, PoiManager.Occupancy.ANY
                ).getOrNull()

                if (nearestBB != null) {
                    source.sendSystemMessage(Component.literal("Found BB at: $nearestBB - ${nearestBB.toVec3().distanceTo(player.position())}"))
                }

                val brain = villager.brain

                val actTime = brain.schedule.getActivityAt((serverWorld.gameTime % 24000L).toInt())

                source.sendSystemMessage(Component.literal("Currently doing: ${actTime.name}"))

                println(brain)

                nearestBB?.let {
                    villager.checkOnBoard(it)
                }

                for (task in brain.runningBehaviors) {
                    println("${task.debugString()} - ${task.status}")
                }

            } else {
                source.sendSystemMessage(Component.literal("Villager was null!"))
            }
        }
    }

    private fun hand() = PlayerCommand {
        val held = it.mainHandItem

        val newPoolEntry = PoolEntry.create().apply {
            content = held.item.id.toString()
            val regOps = RegistryOps.create(JsonOps.INSTANCE, source.level.registryAccess())
            val result = ItemStack.CODEC.encodeStart(regOps, held).resultOrPartial().getOrNull()?.asJsonObject
            components = result?.get("components")?.asJsonObject
        }

        try {
            val saved = newPoolEntry.save()
            it.let {
                it.sendSystemMessage(Component.literal(saved))
                ClipboardCopy(saved).sendToClient(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        1
    }

//    private fun CommandContext<CommandSourceStack>.addToPoolCommand(
//        amt: NumberRange.IntRange,
//        inWorth: Int,
//        func: (amount: IntRange, worth: Int) -> Unit = { a, w -> }
//    ) {
//        val cmd = kambrikCommand<CommandSourceStack> {
//            if (amt.min.getOrNull() == null || amt.max.getOrNull() == null) {
//                source.sendFailure(Component.literal("Amount Range must have a minimum and maximum value!"))
//                return@kambrikCommand
//            }
//
//            func(amt.min.get()..amt.max.get(), inWorth)
//        }
//        cmd.run(this)
//    }

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

            player.sendMessage("Content added.")
            player.sendMessage("Edit §6'config/bountiful/bounty_pools/$poolName.json'§r to edit details.") {
                clickEvent = ClickEvent(ClickEvent.Action.OPEN_FILE, file.absolutePath)
                onHoverShowText { addLiteral("Click to open file '${file.name}'") }
            }
        } else {
            player.sendMessage("Invalid pool name!")
        }

    }

    private fun CommandContext<CommandSourceStack>.addHandToPool(inAmount: IntRange? = null, inUnitWorth: Int? = null, poolName: String) {
        val cmd = PlayerCommand {
            val held = it.mainHandItem

            addToPool(it, inAmount, inUnitWorth, poolName) {
                content = held.id.toString()
                // TODO setting of nbt/components with hand command
                //nbt = held.nbt
            }
            1
        }
        cmd.run(this)
    }

    private fun CommandContext<CommandSourceStack>.addEntityToPool(
        inAmount: IntRange? = null,
        inUnitWorth: Int? = null,
        entityId: ResourceLocation,
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
                println("RARITY WEIGHTS:")
                BountyRarity.entries.forEach { rarity ->
                    println("${rarity.name}\t ${rarity.weightAdjustedFor(rep)}")
                }
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

        source.sendSystemMessage(Component.literal("Bountiful's Decrees & Pools dumped to log."))
    }


}