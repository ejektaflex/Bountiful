package com.example.recipe

import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer


class RecursiveRecipeParser(val server: MinecraftServer) {

    data class RecipeProcess(val makes: Int, val type: RecipeType<*>)

    val recipeManager: RecipeManager
        get() = server.recipeManager

    val visited = mutableMapOf<ItemStack, MutableList<Solveable>>()

    fun hasVisited(stack: ItemStack): Boolean {
        return visited.keys.any { ItemStack.areItemsEqual(it, stack) }
    }

    fun getSolveables(stack: ItemStack): List<Solveable> {
        val key = visited.keys.first { ItemStack.areItemsEqual(it, stack) }
        return visited[key]!!
    }

    fun isTerminating(stack: ItemStack): Boolean {
        return getSolveables(stack).isEmpty()
    }

    val ingredientCosts = mutableMapOf<Ingredient, MutableMap<ItemStack, Solveable>>()

    fun queryAll() {
        for (item in Registries.ITEM) {
            query(ItemStack(item))
        }
    }

    fun query(itemStack: ItemStack) {

        println("Querying $itemStack")

        val producers = recipeManager.values().filter {
            ItemStack.areItemsEqual(it.getOutput(server.registryManager), itemStack)
        }

        recipeManager.values().first().ingredients.first().matchingStacks.toList()

        println("Found ${producers.size} Producers")

        val visitList = visited.getOrPut(itemStack) {
            mutableListOf()
        }

        for (producer in producers) {
            println("\t* Processing Producer: ${producer.id}")

            val solveable = Solveable(producer.ingredients, producer.getOutput(server.registryManager).count, producer.type)

            visitList.add(solveable)

            // Visit all stacks
            for (input in producer.ingredients.map { it.matchingStacks.toList() }.flatten()) {
                if (!hasVisited(input)) {
                    query(input)
                }
            }
        }
        //vis
    }

    data class StackAmount(val item: ItemStack, val takes: Int, val makes: Int)

    fun Ingredient.sameAs(other: Ingredient) = matchingItemIds.toSet() == other.matchingItemIds.toSet()

    val planned = mutableMapOf<ItemStack, Int>()

    fun plan(stack: ItemStack, seen: MutableSet<ItemStack>, deep: Int = 0): Int? {
        val key = visited.stackKey(stack)
        val solves = getSolveables(key)
        seen.add(key)

        val calculated = if (solves.isEmpty()) {
            //println("Terminated at: $stack")
            1
        } else {
            val doot = solves.mapNotNull { it.solve(this, seen.toMutableSet(), deep + 1) }
            return if (doot.isEmpty()) {
                null
            } else {
                doot.minOf { it }
            }
        }
        println("\t".repeat(deep) + "$stack (routes: ${solves.size}) (${calculated})")

        return calculated
    }


    /*

    Bucket
        A)
            * Iron Ingot (3)
                * Iron Nugget (9)
                    * Iron Horse Armor (1)
                    * Chain [Insert] (1)
                    * Iron Helmet
                        * Iron Ingot (5) %STOP%

                * Iron Block (1/9)
                * Raw Iron (1)
                * Iron Ore (1)
                * Deepslate Iron Ore (1)

    Golden Hoe
        A - Gold Ingot (2)
            * Gold Nugget (9)
                * Gold Ingot (1/9) - STRIKE
                * Gold Helmet (1)
                    * Gold Ingot (5) - STRIKE
                * Gold Chestplate (1)
                    * Gold Ingot (8) - STRIKE
                * Gold Boots (1)
                    * Gold Ingot (4) - STRIKE
                * Gold Leggings (1)
                    * Gold Ingot (7) - STRIKE
                * Gold Sword (1)
                    A - Gold Ingot (2) - STRIKE
                    B - Stick (2)
                        * Bamboo (2) - BINGO?
                        * Oak Plank (1/2)
                            * Oak Log (1/4) - BINGO?
            * Gold Block (1/9)
                * Gold Ingot - STRIKE
            * Raw Gold (1) - BINGO!!!!!
            * Gold Ore - BINGO!!!!!
            * Nether Gold Ore - BINGO!!!!!

        B - Wooden Stick (2)
            A - Bamboo (2) - BINGO!
            B - Oak Plank (1/2)
                * Oak Log (1/4) - BINGO!





    Golden Hoe
        A - Gold Ingot (2)
            * Raw Gold (1) - BINGO!!!!!
            * Gold Ore - BINGO!!!!!
            * Nether Gold Ore - BINGO!!!!!

        B - Wooden Stick (2)
            A - Bamboo (2) - BINGO!
            B - Oak Plank (1/2)
                * Oak Log (1/4) - BINGO!

    Ender Chest
        A - Obsidian (8) - BINGO!
        B - Eye of Ender (1)
            * Ender Pearl (1) - BINGO!
            * Blaze Powder (1)
                * Blaze Rod (1/2) - BINGO!






     */

    fun treeEvaluate(
        stack: ItemStack,
        count: Int = stack.count,
        seen: MutableSet<ItemStack> = mutableSetOf(),
        tree: SolveTree = SolveTree(visited.stackKey(stack), count, null)
    ): List<SolveTree> {
        val key = visited.stackKey(stack)
        val toReturn = mutableListOf<SolveTree>()
        for (solve in getSolveables(key)) {
            //println("Solve: $solve")
            for (ingr in solve.ingredients) {
                println(ingr.matchingStacks.toList())
                val singleMatch = ingr.matchingStacks.firstOrNull() ?: continue
                val matchKey = visited.stackKey(singleMatch)
                if (matchKey in seen) {
                    continue
                }
                seen.add(matchKey)
                val leaf =tree.addLeaf(
                    singleMatch,
                    count,
                    solve.type
                )
                for (result in treeEvaluate(leaf.value, leaf.value.count, seen.toMutableSet(), leaf)) {
                    println("Adding to return")
                    toReturn.add(result)
                }
            }
        }
        toReturn.add(tree)
        return toReturn
    }

    fun routes(
        stack: ItemStack,
        seen: MutableSet<ItemStack> = mutableSetOf(),
        path: MutableList<StackAmount> = mutableListOf(),
        resolved: MutableList<List<StackAmount>> = mutableListOf(),
        deep: Int = 1
    ): List<List<StackAmount>> {
        println("R[$deep]: $path")
        val key = visited.stackKey(stack)
        val solves = getSolveables(key)
        val nextItemStacks = solves.map { solve ->
            solve.ingredients
        }

        for (next in nextItemStacks) {
            val stackSums = mutableMapOf<ItemStack, Int>()
            for (ingredient in next) {
                for (option in ingredient.matchingStacks.filter { visited.stackKey(it) !in seen }) {
                    val curr = stackSums.getStackOrPut(option) { 0 }
                    stackSums[option] = curr + option.count
                }
            }


            for ((nextStack, uses) in stackSums) {
                println("${"\t".repeat(deep)} - $nextStack")
                val keyed = visited.stackKey(nextStack)
                val newPath = path.toMutableList().apply {
                    add(StackAmount(keyed, uses, nextStack.count))
                }
                val seenAdded = seen.toMutableSet()
                val newRoutes = routes(nextStack, seenAdded, newPath, resolved, deep = deep + 1)
                for (route in newRoutes) {
                    resolved.add(route)
                }
            }
        }

        //println("resolved: $resolvedRoutes")
        return resolved
    }

    fun trace(
        stack: ItemStack,
        seen: MutableSet<ItemStack> = mutableSetOf(),
        worths: MutableMap<ItemStack, Double> = mutableMapOf(),
        scopeMult: Double = 1.0
    ): MutableMap<ItemStack, Double> {
        val key = visited.stackKey(stack)
        val solves = getSolveables(key)
        seen.add(key)

        println("KEY: $key")

        val nextKeys = solves.map { solve ->
            solve.ingredients.map { ingr ->
                ingr.matchingStacks.toList().map {
                    StackAmount(visited.stackKey(it), it.count, stack.count)
                }
            }.flatten()
        }

        for (list in nextKeys) {
            val stackMap = mutableMapOf<ItemStack, Int>()
            for (stack in list) {
                val oldCount = stackMap.getOrPut(stack.item) { 0 }
                //stackMap[stack.item] = oldCount + stack.
            }
        }

        return worths
    }

    // Given an ItemStack, returns all terminators!
    fun getTerms(
        stack: ItemStack,
        seen: MutableSet<ItemStack> = mutableSetOf(),
        terms: MutableSet<ItemStack> = mutableSetOf()
    ): MutableSet<ItemStack> {
        val key = visited.stackKey(stack)
        val solves = getSolveables(stack)
        seen.add(key)

        println("KEY: $key")

        val nextKeys = solves.map { solve ->
            solve.ingredients.map { it.matchingStacks.toList() }.flatten()
        }.flatten().map {
            visited.stackKey(it)
        }

        if (solves.isEmpty()) {
            terms.add(key)
            return terms
        } else {
            println("\t* NEXT: $nextKeys")
            for (iKey in nextKeys.filter { it !in seen }) {
                getTerms(iKey, seen, terms)
            }
        }
        println("### SEEN: $seen")
        println("### TERM: $terms")
        return terms
    }

    companion object {
        fun Map<ItemStack, *>.stackKey(stack: ItemStack): ItemStack {
            return keys.find { ItemStack.areItemsEqual(it, stack) } ?: stack
        }

        fun <T> MutableMap<ItemStack, T>.getStackOrPut(stack: ItemStack, func: () -> T): T {
            return getOrPut(stackKey(stack), func)
        }
    }

}