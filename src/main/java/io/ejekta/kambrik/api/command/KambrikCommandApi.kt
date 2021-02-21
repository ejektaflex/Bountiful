package io.ejekta.kambrik.api.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class KambrikCommandApi internal constructor() {

    fun hasBasicCreativePermission(c: ServerCommandSource): Boolean {
        if (c.hasPermissionLevel(2) ||
            (c.entity is PlayerEntity && c.player.isCreative)) {
            return true
        }
        return false
    }

    // Meant to be called from inside of a CommandRegistrationCallback event
    fun addCommand(baseCommandName: String, toDispatcher: CommandDispatcher<ServerCommandSource>, func: ArgDsl<ServerLiteralArg>) {
        toDispatcher.register(
            KambrikArgBuilder(
                CommandManager.literal(baseCommandName)
            ).apply(func).finalize()
        )
    }

}