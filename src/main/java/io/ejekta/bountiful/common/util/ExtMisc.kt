package io.ejekta.bountiful.common.util

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.world.World

fun isClient(): Boolean = FabricLoader.getInstance().environmentType == EnvType.CLIENT

fun clientWorld(): World? {
    return if (isClient()) {
        MinecraftClient.getInstance().world
    } else {
        null
    }
}
