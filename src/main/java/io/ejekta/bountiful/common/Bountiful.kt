package io.ejekta.bountiful.common

import net.fabricmc.api.ModInitializer

object Bountiful : ModInitializer {
    const val ID = "bountiful"

    override fun onInitialize() {
        println("Common init")
    }
}