package ejektaflex.bountiful.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.SetupLifecycle
import ejektaflex.bountiful.api.ext.sendErrorMsg
import ejektaflex.bountiful.api.ext.sendMessage
import ejektaflex.bountiful.registry.DecreeRegistry
import ejektaflex.bountiful.registry.PoolRegistry
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands.argument
import net.minecraft.command.Commands.literal

object BountifulCommand {

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
                                literal("reload").executes(reload())
                        )
        )
    }

    private fun reload() = Command<CommandSource> {

        SetupLifecycle.loadContentFromFiles(it.source)

        1
    }

    // TODO If test is true, warn on invalid pool entries
    private fun dump(test: Boolean = false) = Command<CommandSource> {

        it.source.sendMessage("Dumping Decrees to console...")
        for (decree in DecreeRegistry.content) {
            BountifulMod.logger.info("* $decree")
        }
        it.source.sendMessage("Decrees dumped.")

        it.source.sendMessage("Dumping Pools to console...")
        for (pool in PoolRegistry.content) {

            BountifulMod.logger.info("Pool: ${pool.id}")
            for (entry in pool.content) {
                BountifulMod.logger.info("* $entry")
                if (test) {
                    try {
                        entry.validate()
                    } catch (e: Exception) {
                        it.source.sendErrorMsg(e.message!!)
                    }
                }
            }
        }
        it.source.sendMessage("Pools dumped.")

        1
    }


}