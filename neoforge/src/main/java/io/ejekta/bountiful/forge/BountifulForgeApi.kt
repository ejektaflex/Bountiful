package io.ejekta.bountiful.forge

import io.ejekta.bountiful.bridge.BountifulSharedApi
import io.ejekta.bountiful.content.BountifulContent
import net.minecraft.block.ComposterBlock
import net.neoforged.fml.ModList

class BountifulForgeApi : BountifulSharedApi {
    override fun isModLoaded(id: String): Boolean {
        return ModList.get().isLoaded(id)
    }

    override fun registerCompostables() {
        ComposterBlock.registerCompostableItem(0.5f) { BountifulContent.BOUNTY_ITEM }
        ComposterBlock.registerCompostableItem(0.85f) { BountifulContent.DECREE_ITEM }
    }
}