package ejektaflex.bountiful.compat

import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.registry.DecreeRegistry
import ejektaflex.bountiful.item.ItemDecree
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.util.ResourceLocation

@JeiPlugin
class BountifulJEIPlugin : IModPlugin {

    override fun getPluginUid() = ResourceLocation(BountifulMod.MODID, "jeiplugin")

    override fun registerRecipes(reg: IRecipeRegistration) {
        val factory = reg.vanillaRecipeFactory

        println("Decree IDS: " + DecreeRegistry.ids)

        if (DecreeRegistry.ids.isEmpty()) {
            return
        }

        val a = ItemDecree.makeRandomStack()!!
        val b = ItemDecree.makeRandomStack()!!

        val c = ItemDecree.combine(a, b)!!

        factory.createAnvilRecipe(
            a, listOf(b), listOf(c)
        )

    }

}