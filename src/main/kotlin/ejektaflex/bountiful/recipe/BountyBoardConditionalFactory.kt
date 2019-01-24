package ejektaflex.bountiful.recipe

import com.google.gson.JsonObject
import ejektaflex.bountiful.api.BountifulAPI
import net.minecraftforge.common.crafting.IConditionFactory
import net.minecraftforge.common.crafting.JsonContext
import java.util.function.BooleanSupplier

class BountyBoardConditionalFactory : IConditionFactory {
    override fun parse(context: JsonContext, json: JsonObject): BooleanSupplier {
        return BooleanSupplier { BountifulAPI.config.boardRecipeEnabled }
    }
}

