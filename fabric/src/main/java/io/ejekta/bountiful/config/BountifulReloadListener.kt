package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager

object BountifulReloadListener : SimpleSynchronousResourceReloadListener {
    override fun reload(manager: ResourceManager) {
        BountifulIO.contentLoaders.forEach {
            it.clearDestination()
            it.loadData(manager)
        }
    }

    override fun getFabricId() = Bountiful.id("reload_listener")
}