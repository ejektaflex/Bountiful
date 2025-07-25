package io.ejekta.bountiful.forge

import io.ejekta.bountiful.bridge.BountifulSharedApi
import net.neoforged.fml.ModList

class BountifulForgeApi : BountifulSharedApi {
    override fun isModLoaded(id: String): Boolean {
        return ModList.get().isLoaded(id)
    }

    override fun registerCompostables() {
        // no-op (now JSON data-driven in NeoForge in 1.21)
    }
}