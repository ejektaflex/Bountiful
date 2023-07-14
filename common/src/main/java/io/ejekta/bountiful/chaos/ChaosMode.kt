package io.ejekta.bountiful.chaos

import io.ejekta.kambrik.ext.identifier
import net.minecraft.client.MinecraftClient

object ChaosMode {

    // For now, we'll just test clientside. This can easily be moved to server later
    val mc = MinecraftClient.getInstance()

    fun getRecipes() {

        val rm = mc.server?.recipeManager ?: return
        val re = mc.server?.registryManager ?: return
        val recipes = rm.values()
        val solver = ChaosSolver(rm, re)
        //solver.computeTree()

    }

}