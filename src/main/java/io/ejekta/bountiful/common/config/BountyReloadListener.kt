package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener
import net.minecraft.resource.ResourceManager

class BountyReloadListener : SimpleSynchronousResourceReloadListener {

    override fun apply(resourceManager: ResourceManager) {

        val resources = resourceManager.findResources("bountiful/bounties") { it.endsWith(".json") }

        val item = resourceManager.getResource(Bountiful.id("bountiful/afile.json"))

        val lines = item.inputStream.reader().readLines()

    }

    override fun getFabricId() = Bountiful.id("reload_listener")
}
