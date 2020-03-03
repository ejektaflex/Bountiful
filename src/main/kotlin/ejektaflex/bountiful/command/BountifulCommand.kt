package ejektaflex.bountiful.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import ejektaflex.bountiful.BountifulConfig
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.SetupLifecycle
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.data.BountifulResourceType
import ejektaflex.bountiful.item.ItemDecree
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal
import net.minecraft.resources.IReloadableResourceManager
import net.minecraft.resources.IResourceManager
import net.minecraftforge.fml.loading.FMLClientLaunchProvider
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.resource.ReloadRequirements
import net.minecraftforge.resource.SelectiveReloadStateHandler


object BountifulCommand {

    lateinit var genManager: IResourceManager

    fun generateCommand(dispatcher: CommandDispatcher<CommandSource>) {
        dispatcher.register(
                literal("bo")
                        .then(
                                literal("dump").executes(dump())
                        )
                        .then(
                                literal("test").executes(dump(true))
                        )

                        .then(
                                literal("time").then(
                                        argument("num", integer()).executes { c ->
                                            bSpeed(getInteger(c, "num"))
                                            1
                                        }
                                )
                        )

                        .then(
                                literal("dec").then(
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
                                literal("reload").executes(reload())
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

    private fun bSpeed(num: Int) = Command<CommandSource> {

        BountifulConfig.SERVER.boardAddFrequency.set(num)

        1
    }

    private fun reload() = Command<CommandSource> {

        //SetupLifecycle.loadContentFromFiles(it.source)

        println("Reloading on side! :O isRemote?: ${it.source.world.isRemote}")

        BountifulResourceType.values().forEach { type ->
            BountifulMod.reloadBountyData(it.source.server, it.source.server.resourceManager, type, it.source)
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

            SetupLifecycle.validatePool(pool, it.source, test)

        }
        it.source.sendMessage("Pools dumped.")

        1
    }


}