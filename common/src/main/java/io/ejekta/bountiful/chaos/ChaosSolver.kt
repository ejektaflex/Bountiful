package io.ejekta.bountiful.chaos

import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.registry.DynamicRegistryManager
import net.minecraft.util.Identifier

class ChaosSolver(recipeManager: RecipeManager, private val regManager: DynamicRegistryManager) {

    private val recipeMap = recipeManager.values().toList().associateBy { it.id }

    val tree = ChaosTree()

    val rawDepMap = mutableMapOf<ItemStack, MutableSet<ItemStack>>()

    val itemLookup = recipeManager.values().toList().groupBy { it.getOutput(regManager).item }

    val recipeLookup = recipeManager.values().toList().associateBy { it.getOutput(regManager) }

//    fun computeTree() {
//
//        println("Computing...")
//
//        for ((id, recipe) in recipeMap) {
//            val out = recipe.getOutput(regManager)
//            val inp = recipe.ingredients.map { it.matchingStacks.toList() }.flatten().toSet()
//            val curr = rawDepMap.getOrPut(out) { mutableSetOf() }
//            curr.addAll(inp)
//        }
//
//        for (regItem in regManager[RegistryKeys.ITEM]) {
//            if (rawDepMap.keys.none { it.item == regItem }) {
//                println(" TERMINATED: ${regItem.identifier}")
//            }
//        }
//
//        for ((rawIn, rawList) in rawDepMap) {
//            println(rawIn.identifier)
//            println("   * ${rawList.map { it.identifier }}")
//        }
//    }

    // List of possible
    val solveMap = mutableMapOf<ItemStack, MutableList<MutableList<ItemStack>>>()

    // Precomputed is a shared pool between all recursions making sure we aren't computing more than once
//    fun compute(stack: ItemStack, precomputed: Set<ItemStack> = setOf()): List<ItemStack> {
//        val key = stack
//
//        val recipe = getRecipe(stack) ?: return emptyList()
//
//
//        solveMap[stack] = mutableListOf()
//
//        for (itemPossList in recipe.ingredients.map { it.matchingStacks }) {
//            for (itemPoss in itemPossList) {
//                if (itemPoss !in solveMap && itemPoss !in precomputed) {
//                    solveMap[itemPoss] = listOf(itemPoss)
//                }
//            }
//        }
//    }

    fun runThroughOnce() {
        val id = Identifier("iron_axe")
        val recipe = recipeMap.getValue(id)
        val cache = mutableMapOf<ItemStack, MutableList<ItemStack>>()
        //algorithm(recipe, cache = cache)
    }

    class ItemPath {
        val stackList = mutableListOf<ItemStack>()
        val weightList = mutableListOf<Double>()

        fun addToPath(stack: ItemStack, factor: Double) {
            stackList.add(stack)
        }
    }

    fun algorithm(
        recipe: Recipe<*>,
        path: MutableList<ItemStack> = mutableListOf(),
        // cache holds all paths to making that itemstack
        cache: MutableMap<ItemStack, MutableList<MutableList<ItemStack>>> = mutableMapOf() // shared cache across all recursions
    ) {
        val out = recipe.getOutput(regManager)
        val ings = recipe.ingredients

        path.add(out)

        for (ing in ings) {
            val matches = ing.matchingStacks
            when (matches.size) {
                0 -> {

                }
                1 -> {
                    //
                }
            }
            for (opt in ing.matchingStacks) {
                if (opt in cache) {
                    // Cyclic!
                    continue
                } else {
                    val looked = itemLookup[opt.item]

                    if (looked == null) {
                        // no recipe for this, it's a terminator
                    }
                }
            }
        }
    }



//    fun computeFor(stack: ItemStack, path: MutableList<ItemStack> = mutableListOf()): List<Set<ItemStack>> {
//        val recipe = itemLookup[stack.item]
//        if (recipe == null) { // we reached a terminator
//
//        }
//    }

    fun createTree() {
        val id = Identifier("iron_axe")
        val recipe = recipeMap.getValue(id)
        val out = recipe.getOutput(regManager)
        val root = MatNode(out)

        recipe.ingredients.first().matchingStacks

        //val abc = Ingredient.

        //toTree(root)
    }

//    fun toTree(matNode: MatNode) {
//        val recipes = itemLookup[matNode.stack.item] ?: emptyList()
//        for (recipe in recipes) {
//            val ings = recipe.ingredients
//            val ingCach = mutableMapOf<ItemStack, Int>()
//            for (ing in ings) {
//                val firstMatchCount = ing.matchingStacks.firstOrNull() ?: continue
//                ingCach[firstMatchCount] = (ingCach[firstMatchCount] ?: 0) + firstMatchCount.count
//            }
//
//        }
//    }

    fun toTree(matSet: MatSet) {
        //for ()
    }

    /*


    Take the iron axe:
    IRON AXE:
        * IRON_INGOT 3 (cyclic bc leaves are)
            * IRON_NUGGET (cyclic bc leaves are) 9
                * IRON_INGOT 1/9 (CYCLIC!)

                * IRON_AXE 1 (CYCLIC!)

                * IRON_SWORD 1
                    * IRON_INGOT 2 (CYCLIC!)
                    * STICK 1
                        * OAK PLANK 1/2
                            * OAK LOG 1/4

                        * BIRCH PLANK 1/2
                            * BIRCH LOG 1/4

                        * JUNGLE PLANK 1/2
                            * JUNGLE LOG 1/4

                * IRON_HELMET 1
                    * IRON_INGOT 5 (CYCLIC!)

            * IRON_ORE 1 (not cyclic, is terminator)
        * STICK 2
            * PLANK 1/2
                * LOG 1/4


    At the end of the day,
    LOG will terminate, PLANK and STICK will depend on LOG

    would this be more useful?:

    LOG 1
        PLANK 4
            STICK 2

            CRAFTING_TABLE 1/4

    IRON_NUGGET 1
        IRON_SWORD 1
            IRON_INGOT

        IRON_AXE 1

        IRON_HELMET 1

        IRON_INGOT 1/9



    Maybe that's not best. What about this?:

    iron_axe 1
        iron_ingot 3
            iron_nugget 9
                iron_sword 1 (cc)
                    iron_ingot 2 (c, 2*1*9 = 18, which is >= 1, so it's not a cycle worth following)
                    stick 1
                        plank 1/2
                            log 1/4

                        bamboo 2

                iron_helmet 1 (cc for above reasons)

                iron_axe 1 (cc for above reasons)

                iron_ingot 1/9 (1/9 * 9 = 1, which is >= 1, so it's not a cycle worth following)

            iron_block 1/9 (cc)
                iron_ingot 9 (9 * 1/9 = 1, which is >= 1, so it's not a cycle worth following)

            raw_iron 1

            iron_ore 1

    After trimming out cycles and child-based cycles, we just get this:

    iron_axe 1
        (ING 1)
        stick 2
            plank 1/2
                log 1/4

                wood 1/4
                    log 1

            bamboo 2

        (ING 2)
        iron_ingot 3
            iron_nugget 3

        raw_iron 1

        iron_ore 1

     */

}