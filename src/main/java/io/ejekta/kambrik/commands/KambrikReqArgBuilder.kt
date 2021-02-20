package io.ejekta.kambrik.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

class KambrikReqArgBuilder<A : RequiredArgumentBuilder<ServerCommandSource, *>>(arg: A) : KambrikArgBuilder<A>(arg) {



}