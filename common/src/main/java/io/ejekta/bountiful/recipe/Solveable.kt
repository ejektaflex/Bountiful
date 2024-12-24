package io.ejekta.bountiful.recipe

import io.ejekta.bountiful.recipe.RecursiveRecipeParser.Companion.stackKey
import io.ejekta.kambrik.ext.id
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeType

class Solveable(val ingredients: List<Ingredient>, val makes: Int, val type: RecipeType<*>) {
    fun solve(parser: RecursiveRecipeParser, seen: MutableSet<ItemStack>, deep: Int): Int? {
        val routes = ingredients.map { ingr ->
            val staks = ingr.items.toList().filter { parser.visited.stackKey(it) !in seen }

            if (staks.isEmpty()) {
                return null
            }

            staks.mapNotNull { stack ->
                if (stack !in parser.planned) {
                    val calc = parser.plan(stack, seen.toMutableSet(), deep + 1)
                    calc?.let { parser.planned[stack] = it }
                }
                parser.planned[stack]
            }.minOrNull()
        }.filterNotNull()

        //println("\t".repeat(deep) + "routes (${routes.size})")
        return if (routes.isEmpty()) {
            null
        } else {
            routes.sum()
        }
    }

    override fun toString(): String {
        return "Solveable(ingredients=${ingredients.map { 
            ingredient -> ingredient.items.map { it.id }.joinToString("/") 
        }}, makes=$makes, type=$type)"
    }

}