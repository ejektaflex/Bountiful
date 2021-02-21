package io.ejekta.kambrik.api.command.types

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity

class PlayerCommand(val func: CommandContext<ServerCommandSource>.(player: ServerPlayerEntity) -> Int) : Command<ServerCommandSource> {

    override fun run(ctx: CommandContext<ServerCommandSource>): Int {
        return func(ctx, ctx.source.player)
    }

}