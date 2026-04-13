package io.ejekta.bountiful

import io.ejekta.bountiful.bridge.BountifulSharedApi
import io.ejekta.bountiful.content.BountifulContent
import net.fabricmc.fabric.api.registry.CompostableRegistry
import net.fabricmc.loader.api.FabricLoader

class BountifulFabricApi : BountifulSharedApi {
    override fun isModLoaded(id: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(id)
    }

    override fun registerCompostables() {
        CompostableRegistry.INSTANCE.add(BountifulContent.BOUNTY_ITEM, 0.5f)
        CompostableRegistry.INSTANCE.add(BountifulContent.DECREE_ITEM, 0.85f)
    }
}
