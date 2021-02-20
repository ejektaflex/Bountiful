package io.ejekta.kambrik.commands

import com.mojang.brigadier.Message
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import io.ejekta.kambrik.ext.addAll
import net.minecraft.server.command.ServerCommandSource


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

