package io.ejekta.kambrik.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource



class KambrikLiteral<S>(lit: String) : LiteralArgumentBuilder<S>(lit) {
    operator fun String.invoke(func: KambrikLiteral<S>.() -> Unit) {
        then(
            KambrikLiteral<S>(this).apply(func)
        )
    }
    infix fun String.execs(cmd: Command<S>) {
        this {
            executes(cmd)
        }
    }
}

fun kambrikLiteral(base: String, func: KambrikLiteral<ServerCommandSource>.() -> Unit): KambrikLiteral<ServerCommandSource> {
    return KambrikLiteral<ServerCommandSource>(base).apply(func)
}

fun ArgumentBuilder<ServerCommandSource, *>.literal(word: String, func: KambrikLiteral<ServerCommandSource>.() -> Unit) {
    kambrikLiteral(word, func)
}

fun LiteralArgumentBuilder<ServerCommandSource>.literalExecutes(word: String, cmd: Command<ServerCommandSource>) {
    literal(word) {
        executes(cmd)
    }
}



fun <T> ArgumentBuilder<ServerCommandSource, *>.argument(word: String, type: ArgumentType<T>, func: RequiredArgumentBuilder<ServerCommandSource, T>.() -> Unit) {
    then(
        CommandManager.argument(word, type).apply(func)
    )
}

fun <T> ArgumentBuilder<ServerCommandSource, *>.argumentExecutes(word: String, type: ArgumentType<T>, cmd: Command<ServerCommandSource>) {
    argument(word, type) {
        executes(cmd)
    }
}

fun ArgumentBuilder<ServerCommandSource, *>.stringArgExecutes(
    word: String,
    cmd: Command<ServerCommandSource>,
    suggests: List<String>? = null,
    func: RequiredArgumentBuilder<ServerCommandSource, String>.() -> Unit = {}
) {
    argument(word, string()) {
        executes(cmd)
        apply(func)
        suggests?.let {
            suggests { _, builder ->
                for (item in it) {
                    builder.suggest(item)
                }
                builder.buildFuture()
            }
        }
    }
}

fun ArgumentBuilder<ServerCommandSource, *>.intArgExecutes(
    word: String,
    cmd: Command<ServerCommandSource>,
    suggests: List<String>? = null,
    func: RequiredArgumentBuilder<ServerCommandSource, Int>.() -> Unit = {}
) {
    argument(word, integer()) {
        executes(cmd)
        apply(func)
        suggests?.let {
            suggests { _, builder ->
                for (item in it) {
                    builder.suggest(item)
                }
                builder.buildFuture()
            }
        }
    }
}




