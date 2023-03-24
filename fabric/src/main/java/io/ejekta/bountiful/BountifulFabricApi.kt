package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.BountifulSharedApi
import io.ejekta.bountiful.content.messages.*
import io.ejekta.kambrik.Kambrik
import net.fabricmc.loader.api.FabricLoader

class BountifulFabricApi : BountifulSharedApi {
    override fun isModLoaded(id: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(id)
    }
}