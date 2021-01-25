package ejektaflex.bountiful.data.json

import com.google.common.collect.ImmutableMap
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import ejektaflex.bountiful.BountifulMod
import ejektaflex.bountiful.data.bounty.enums.BountifulResourceType
import ejektaflex.bountiful.data.bounty.enums.BountyType
import net.minecraft.client.resources.JsonReloadListener
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.IRecipeType
import net.minecraft.item.crafting.RecipeManager
import net.minecraft.profiler.IProfiler
import net.minecraft.resources.IResourceManager
import net.minecraft.util.JSONUtils
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.crafting.CraftingHelper
import java.lang.IllegalArgumentException
import java.util.function.Function

class BountyReloadListener : JsonReloadListener(JsonAdapter.gson, "bounties") {

    override fun apply(
        objectIn: MutableMap<ResourceLocation, JsonElement>,
        resourceManagerIn: IResourceManager,
        profilerIn: IProfiler
    ) {

        for ((rl, element) in objectIn) {
            if (rl.path.startsWith("_")) { continue }

            println("BoReloader wants to load resource at: $rl. It is: $element")
            val toGrab = rl.path.substringBefore('/')
            println("TOGRAB: $toGrab")
            val typeOfFile = BountifulResourceType.values().find { it.folderName == toGrab } ?: continue // ignore non resource types!
            println("BoReloader has type of file: $typeOfFile")
            val loaded = JsonAdapter.fromJson(element, typeOfFile.klazz)
            println("BoReloader loaded this: $loaded")


        }


    }


}