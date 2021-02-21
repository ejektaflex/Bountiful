package io.ejekta.kambrik.api.command

import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.ejekta.kambrik.api.command.types.PlayerCommand
import io.ejekta.kambrik.ext.addAll
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity


internal typealias ServerLiteralArg = LiteralArgumentBuilder<ServerCommandSource>
internal typealias ServerRequiredArg = RequiredArgumentBuilder<ServerCommandSource, *>
internal typealias ArgDsl<T> = KambrikArgBuilder<T>.() -> Unit
internal typealias ReqArgDsl<A> = KambrikArgBuilder<ServerRequiredArg>.(value: A) -> Unit

fun suggestionList(func: () -> List<String>): SuggestionProvider<ServerCommandSource> {
    return SuggestionProvider<ServerCommandSource> { context, builder ->
        builder.addAll(func())
        builder.buildFuture()
    }
}

fun suggestionListTooltipped(func: () -> List<Pair<String, Message>>): SuggestionProvider<ServerCommandSource> {
    return SuggestionProvider<ServerCommandSource> { _, builder ->
        for ((item, msg) in func()) {
            builder.suggest(item, msg)
        }
        builder.buildFuture()
    }
}

fun playerCommand(player: CommandContext<ServerCommandSource>.(player: ServerPlayerEntity) -> Int): PlayerCommand {
    return PlayerCommand(player)
}

fun CommandContext<ServerCommandSource>.getString(name: String): String = StringArgumentType.getString(this, name)
fun CommandContext<ServerCommandSource>.getInt(name: String): Int = IntegerArgumentType.getInteger(this, name)

