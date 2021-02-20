package io.ejekta.kambrik

import com.mojang.brigadier.CommandDispatcher
import io.ejekta.kambrik.commands.ArgDsl
import io.ejekta.kambrik.commands.KambrikArgBuilder
import io.ejekta.kambrik.commands.ServerLiteralArg
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object Kambrik {

    fun addCommand(baseCommandName: String, toDispatcher: CommandDispatcher<ServerCommandSource>, func: ArgDsl<ServerLiteralArg>) {
        toDispatcher.register(
            KambrikArgBuilder(
                CommandManager.literal(baseCommandName)
            ).apply(func).finalize()
        )
    }

}