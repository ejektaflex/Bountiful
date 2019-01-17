package ejektaflex.bountiful.recipe

import com.google.gson.JsonObject
import ejektaflex.bountiful.api.BountifulAPI
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.JsonUtils
import net.minecraftforge.common.crafting.CraftingHelper
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.IIngredientFactory
import net.minecraftforge.common.crafting.JsonContext
import java.util.function.BooleanSupplier

class BountyBoardConditionalFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject): BooleanSupplier {
        return BooleanSupplier { BountifulAPI.config.boardRecipeEnabled }
    }
}

