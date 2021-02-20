package io.ejekta.kambrik.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.ServerCommandSource


internal typealias ServerLiteralArg = LiteralArgumentBuilder<ServerCommandSource>
internal typealias ServerRequiredArg = RequiredArgumentBuilder<ServerCommandSource, *>
internal typealias ArgDsl<T> = KambrikArgBuilder<T>.() -> Unit
