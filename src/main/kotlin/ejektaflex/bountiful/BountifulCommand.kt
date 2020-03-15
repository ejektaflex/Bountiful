package ejektaflex.bountiful

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import ejektaflex.bountiful.data.bounty.BountyEntryItem
import ejektaflex.bountiful.ext.sendErrorMsg
import ejektaflex.bountiful.ext.sendMessage
import ejektaflex.bountiful.ext.supposedlyNotNull
import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.json.JsonAdapter
import ejektaflex.bountiful.item.ItemDecree
import ejektaflex.bountiful.logic.BountyCreator
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.data.registry.PoolRegistry
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.resources.IResourceManager
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraft.world.World
import net.minecraftforge.items.ItemHandlerHelper


object BountifulCommand {

    lateinit var genManager: IResourceManager

    fun generateCommand(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
                literal("bo")

                        .then(
                                literal("test")
                                        .requires(::hasPermission)
                                        .executes(dump(true))
                        )

                        .then(
                                literal("decree")
                                        .requires(::hasPermission)
                                        .then(
                                        argument("decType", string())
                                                .suggests { c, b ->
                                                    for (dec in DecreeRegistry.content) {
                                                        b.suggest(dec.id)
                                                    }
                                                    b.buildFuture()
                                                }
                                                .executes { c ->

                                                    val decId = getString(c, "decType")
                                                    val stack = ItemDecree.makeStack(decId)

                                                    if (stack != null) {

                                                        ItemHandlerHelper.giveItemToPlayer(
                                                                c.source.asPlayer(),
                                                                stack,
                                                                c.source.asPlayer().inventory.currentItem
                                                        )

                                                    } else {
                                                        c.source.sendMessage("Decree ID $decId not found")
                                                    }


                                                    1
                                                }
                                )
                        )

                        .then(
                                literal("sample")
                                        .requires(::hasPermission)
                                        .then(
                                        argument("decType", string())
                                                .suggests { c, b ->
                                                    for (dec in DecreeRegistry.content) {
                                                        b.suggest(dec.id)
                                                    }
                                                    b.buildFuture()
                                                }
                                                .executes(
                                                        sample(1)
                                                )
                                                .then(
                                                        argument("safety", integer())
                                                                .suggests { c, b ->
                                                                    b.suggest(1)
                                                                    b.suggest(2)
                                                                    b.buildFuture()
                                                                }
                                                                .executes(
                                                                        sample(-1)
                                                                )
                                                )
                                )
                        )

                        .then(
                                literal("hand")
                                        .executes(hand())
                        )


                        .then(
                                literal("reload")
                                        .requires(::hasPermission)
                                        .executes(reload())
                        ).apply {
                            if (BountifulMod.config.debugMode) {
                                /*
                                then(
                                        literal("debug_reinitDefaultContent").executes(reinitDefaultContent())
                                )

                                 */
                            }
                        }
        )
    }

    fun hasPermission(c: CommandSource): Boolean {
        if (c.hasPermissionLevel(2) ||
                (c.entity is PlayerEntity && c.asPlayer().isCreative)) {
            return true
        }
        return false
    }



    private fun hand() = Command<CommandSource> {

        if (it.source.entity is PlayerEntity) {
            val player = it.source.asPlayer()

            val holding = player.heldItemMainhand

            val newEntry = BountyEntryItem().apply {
                content = holding.item.registryName.toString()
                amount = holding.count
                if (holding.hasTag()) {
                    jsonNBT = JsonAdapter.parse(holding.tag.toString())
                }
                unitWorth = 1000
            }

            val asText = JsonAdapter.toJson(newEntry)


            println("AM side: ${it.source.world.isRemote}")
            try {
                Minecraft.getInstance().keyboardListener.clipboardString = asText
            } catch (e: Exception) {
                // Ignore
            }

            val msg = StringTextComponent("§aItem: §9${holding.item.registryName}§r, §aBounty Entry§r: §6[Hover]§r").apply {
                style.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, StringTextComponent("§6Bounty Entry (Click to copy to Clipboard):\n").appendSibling(
                        StringTextComponent(asText).apply {
                            style.color = TextFormatting.DARK_PURPLE
                        }
                ))
            }

            it.source.sendFeedback(msg, true)

        } else {
            it.source.sendErrorMsg("Must be a player to check their hand")
        }

        1
    }

    private fun reload() = Command<CommandSource> {

        BountifulResourceType.values().forEach { type ->
            BountifulMod.reloadBountyData(it.source.server, it.source.server.resourceManager, type, it.source)
        }

        1
    }

    private fun sample(inSafety: Int) = Command<CommandSource> {

        val safety = if (inSafety < 0) {
            getInteger(it, "safety")
        } else {
            inSafety
        }

        val decreeName = getString(it, "decType")

        it.source.sendMessage("§6Sampling...")

        val decree = DecreeRegistry.getDecree(decreeName)
        val log = BountifulMod.logger

        if (decree == null) {
            it.source.sendErrorMsg("Decree '$decreeName' does not exist!")
            return@Command 1
        }

        val objs = DecreeRegistry.getObjectives(listOf(decree))

        if (objs.isEmpty()) {
            it.source.sendErrorMsg("Cannot sample decree '$decreeName' as it has no valid objectives!")
        }

        val rewards = DecreeRegistry.getRewards(listOf(decree))

        if (rewards.isEmpty()) {
            it.source.sendErrorMsg("Cannot sample decree '$decreeName', as there are no valid rewards for it!")
        }

        for (reward in rewards) {

            // Since we can have at most 2 objectives, lets assume that worst case all 3 had this value
            val worthToMatch = reward.maxWorth * safety

            val within = BountyCreator.getObjectivesWithinVariance(
                    DecreeRegistry.getObjectives(listOf(decree)),
                    worthToMatch,
                    0.2
            )

            val nearest = BountyCreator.pickObjective(supposedlyNotNull(objs), worthToMatch).pick(worthToMatch)

            if (within.isEmpty()) {
                it.source.sendMessage("§cDecree can't handle theoretical bounty of $safety of §4${reward.amountRange.max}x[${reward.content}]§c, next closest obj was: §4${nearest.amount}x[${nearest.content}]§c")
                it.source.sendErrorMsg("- * §cNeeded: §4$worthToMatch§c, had: §4${nearest.calculatedWorth}§c")
            } else {
                it.source.sendMessage("§2Matched: 2x[${reward.content}] with ${within.size} objectives!")
                it.source.sendMessage("- * §5${within.joinToString("§f, §5") { thing -> thing.content }}")
            }


        }



        1
    }

    // TODO If test is true, warn on invalid pool entries
    private fun dump(test: Boolean = false) = Command<CommandSource> {

        it.source.sendMessage("Dumping Decrees to console")
        for (decree in DecreeRegistry.content) {
            BountifulMod.logger.info("* $decree")
        }
        it.source.sendMessage("Decrees dumped.")

        it.source.sendMessage("Dumping Pools to console...")
        for (pool in PoolRegistry.content) {

            SetupLifecycle.validatePool(pool, it.source, true)

        }
        it.source.sendMessage("Pools dumped.")

        1
    }


}