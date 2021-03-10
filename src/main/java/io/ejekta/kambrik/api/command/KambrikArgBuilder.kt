package io.ejekta.kambrik.api.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.tree.CommandNode
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class KambrikArgBuilder<A : ArgumentBuilder<ServerCommandSource, *>>(val arg: A) :
    ArgumentBuilder<ServerCommandSource, KambrikArgBuilder<A>>() {

    private val subArgs = mutableListOf<KambrikArgBuilder<*>>()

    internal fun finalize(): A {
        for (subArg in subArgs) {
            arg.then(subArg.arg)
        }
        return arg
    }

    fun literal(word: String, func: ArgDsl<ServerLiteralArg>) {
        val req = KambrikArgBuilder<ServerLiteralArg>(CommandManager.literal(word)).apply(func)
        req.finalize()
        subArgs.add(req)
    }

    fun <T> argument(
        type: ArgumentType<T>,
        word: String,
        items: SuggestionProvider<ServerCommandSource>?,
        func: ArgDsl<ServerRequiredArg> = {}
    ): ServerRequiredArg {
        val req = KambrikArgBuilder<ServerRequiredArg>(CommandManager.argument(word, type)).apply(func)

        items?.let {
            req.arg.suggests(items)
        }

        req.finalize()
        subArgs.add(req)
        return req.arg
    }

    @Suppress("UNCHECKED_CAST")
    override fun executes(command: Command<ServerCommandSource>?): KambrikArgBuilder<A> {
        return KambrikArgBuilder(arg.executes(command) as A)
    }

    fun stringArg(
        word: String, items: SuggestionProvider<ServerCommandSource>? = null, func: ArgDsl<ServerRequiredArg> = {}
    ) = argument(string(), word, items, func)

    fun intArg(
        word: String, range: IntRange? = null,
        items: SuggestionProvider<ServerCommandSource>? = null, func: ArgDsl<ServerRequiredArg> = {}
    ) = argument(if (range != null) integer(range.first, range.last) else integer(), word, items, func)

    fun floatArg(
        word: String, range: ClosedFloatingPointRange<Float>? = null,
        items: SuggestionProvider<ServerCommandSource>? = null, func: ArgDsl<ServerRequiredArg> = {}
    ) = argument(if (range != null) FloatArgumentType.floatArg(range.start, range.endInclusive) else FloatArgumentType.floatArg(), word, items, func)

    operator fun String.invoke(func: ArgDsl<ServerLiteralArg>) {
        literal(this, func)
    }

    infix fun String.runs(cmd: Command<ServerCommandSource>) {
        this { this.executes(cmd) }
    }

    infix fun ServerRequiredArg.runs(cmd: Command<ServerCommandSource>) {
        this@runs.executes(cmd)
    }

    override fun getThis(): KambrikArgBuilder<A> = this

    override fun build(): CommandNode<ServerCommandSource> {
        return arg.build()
    }

}