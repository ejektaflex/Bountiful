package io.ejekta.kambrik.ext

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer

fun ifModPresent(modid: String, func: (container: ModContainer) -> Unit) {
    FabricLoader.getInstance().getModContainer(modid).ifPresent(func)
}

fun ifModsPresent(vararg modid: String, func: () -> Unit) {
    val containers = modid.toList().map {
        FabricLoader.getInstance().isModLoaded(it)
    }.all { true }
}

