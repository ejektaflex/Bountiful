package io.ejekta.bountiful.chaos

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.bountiful.util.get
import io.ejekta.bountiful.util.getNullable
import io.ejekta.kambrik.ext.id
import kotlinx.serialization.Serializable
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeManager
import java.util.*
import kotlin.jvm.optionals.getOrNull

class DepthSolver(val server: MinecraftServer, val data: BountifulChaosData, val info: BountifulChaosInfo) {

    private val recipeManager: RecipeManager = server.recipeManager
    private val regManager = server.registryAccess()

    private val terminators = mutableSetOf<ResourceLocation>()
    private val deps = mutableMapOf<ResourceLocation, MutableSet<ResourceLocation>>()

    // Final cost map
    private val costMap = mutableMapOf<Item, Double>()
    private val pathLenMap = mutableMapOf<Item, Int>()
    private val matchCosts = mutableMapOf<Item, Double>()
    private val ignoreSet = mutableSetOf<Item>()

    fun costOf(item: Item): Double? {
        return costMap[item] ?: matchCosts[item]
    }

    // Populate cost map from config
    init {
        Bountiful.LOGGER.debug("Init solve system..")


        for (item in server[Registries.ITEM]) {
            val matchCost = data.matching.matchCost(item, server)
            if (matchCost != null) {
                if (!data.matching.isIgnored(item)) {
                    Bountiful.LOGGER.debug("Adding match cost: ${item.id}")
                    matchCosts[item] = matchCost
                } else {
                    ignoreSet.add(item)
                }
            }
        }

        for (itemId in data.required.filter { it.value != null }.keys) {
            val item = server[Registries.ITEM].getOptional(itemId).getOrNull()
            item?.let {
                if (it !in matchCosts) {
                    costMap[it] = data.required[itemId]!!
                }
            }
        }
    }

    private val ItemStack.recipes: List<RecipeHolder<*>>
        get() = stackLookup[this.item] ?: emptyList()

    private val RecipeHolder<*>.inItemSets: List<Set<ItemStack>>
        get() = this.value.ingredients.map { it.items.toSet() }

    private val stackLookup = recipeManager.recipes.toList().groupBy {
        it.value.getResultItem(regManager).item
    }

    @JvmRecord
    data class SolveResult(val amt: Double, val path: List<ItemStack>)

    // Attempts to solve for stack cost.
    fun solveFor(stack: ItemStack, path: List<ItemStack>): SolveResult? {
        val recipes = stack.recipes

        // If no recipe exists, it is a terminator.
        if (recipes.isEmpty()) {
            terminators.add(stack.id!!)
            // Add dependency on said item
            for (pathItem in path) {
                deps.getOrPut(stack.id!!) { mutableSetOf() }.add(pathItem.id!!)
            }
            return null
        }

        val recipeCosts = mutableListOf<Double>()

        for (recipe in recipes) {
            //val inputCounts = recipe.inItemSets.flatten().groupBy { it.item }.map { it.key to it.value.sumOf { stack -> stack.count } }.toMap()

            var ingredientRunningCost = 0.0

            val inputSets = recipe.inItemSets

            var numUnsolvedIngredients = 0

            for (optionSet in inputSets) {
                // Currently grabs the first solved and adds the cost; This assumes tag ingredients all have the same cost;
                // It might be useful to average them and not just grab the first one in the future!
                for (option in optionSet) {
                    if (option.item in path.map { it.item } || path.size > 24) { // Cyclic dependency or too much recursion!
                        continue
                    }

                    val itemCost = costOf(option.item) // Check for already calculated cost, simple O(1) lookup for worth
                    if (itemCost != null) {
                        // Add the cost of the item times the amount needed (in that slot)
                        ingredientRunningCost += itemCost * option.count
                        numUnsolvedIngredients -= 1
                        break
                    } else {
                        // No calculated cost. Can we iteratively find one?
                        val solvedCost = solveFor(option, path + stack)
                        if (solvedCost != null) {
                            ingredientRunningCost += solvedCost.amt * option.count
                            numUnsolvedIngredients -= 1
                            break
                        }
                    }
                }
            }

            // If a recipe makes 3 of something, the actual cost is only 1/3 as much
            ingredientRunningCost /= recipe.value.getResultItem(regManager).count

            if (numUnsolvedIngredients > 0) {
                //println("Could not solve for ${stack.identifier}".padStart(padding))
            } else {
                //println("Resolved all ingredients for: ${stack.identifier}".padStart(padding))
                recipeCosts.add(ingredientRunningCost)
                //println("Cost was: $ingredientRunningCost".padStart(padding))
            }

        }

        val finalCost = recipeCosts.minOrNull()

        // Of all calculated recipe costs, find the minimum
        finalCost?.let {
            costMap[stack.item] = it
            pathLenMap[stack.item] = path.size // may not accurately reflect path len?
        }

        return finalCost?.let { SolveResult(it, path) }
    }

    fun solveRequiredRecipes() {
        var unsolved = 0
        val unsolvedList = mutableListOf<Item>()
        val allItemSet = mutableSetOf<Item>()
        for (item in regManager.registry(Registries.ITEM).get()) {
            allItemSet.add(item)
            // If there exists a recipe for it, solve it
            if (item in stackLookup.keys) {
                val didSolve = solveFor(ItemStack(item), emptyList())
                if (didSolve == null && data.required[item.id!!] == null) {
                    unsolved += 1
                    unsolvedList.add(item)
                }
            } else {
                unsolved += 1
                unsolvedList.add(item)
            }
        }

        info.redundant = matchCosts.keys.map { it.id!! }.sorted().toMutableList()
        info.unsolvedList = (allItemSet.filter { !data.matching.isIgnored(it) }.toSet() - matchCosts.keys - costMap.keys).map { it.id!! }.toList().sorted()

        println("hi")
        //info.unsolvedList = unsolvedList.map { it.id!! }.sorted() - info.redundant
    }

    fun syncConfig() {

        Bountiful.LOGGER.debug("Syncing Chaos Config...")


        // Remove redundant required items
        for ((id, amt) in data.required.toList()) {
            if (id in info.redundant) {
                data.required.remove(id)
            }
        }

        Bountiful.LOGGER.debug("Terminators:")
        // Insert terminators into required prop
        for (line in terminators.sorted() - info.redundant.toSet()) {
            data.required[line] = costMap[regManager[Registries.ITEM].getNullable(line)]
            Bountiful.LOGGER.debug(line)
        }
        // Reset JSON file ordering (this is a bit hacky)
        data.required = data.required.toList().sortedBy { it.first.toString() }.toMap().toMutableMap()
        // Update dependency numbering
        //info.deps = deps.map { it.key to it.value.size }.sortedBy { -it.second }.toMap().toMutableMap()
        info.deps = deps.map { it.key to it.value }.toMap().toSortedMap()

        for (item in regManager[Registries.ITEM].sortedBy { it.id }) {
            val isIgnored = data.matching.isIgnored(item)
            if (costOf(item) == null && item.id !in info.redundant && !isIgnored) {
                data.optional[item.id!!] = null
            } else {
                data.optional.remove(item.id)
            }
        }

    }

    fun sendToRegistries() {
        Bountiful.LOGGER.debug("Sending chaos data to Bountiful registries..")

        BountifulContent.populatePools(emptyList())
        BountifulContent.Decrees.clear()

        val poolId = "chaos"
        val pool = Pool(poolId)

        for (item in regManager[Registries.ITEM].sortedBy { it.id }) {
            println("Item: ${item.id.toString().padEnd(50)} - ${costOf(item).toString().padEnd(16)} - ${pathLenMap[item]}")
            val realCost = costOf(item) ?: continue
            val stack = ItemStack(item)
            val realAmtMax = stack.maxStackSize
            var realRarity = when (stack.rarity) {
                Rarity.UNCOMMON -> BountyRarity.RARE
                Rarity.RARE -> BountyRarity.EPIC
                Rarity.EPIC -> BountyRarity.LEGENDARY
                else -> BountyRarity.COMMON
            }

            if (stack.rarity == Rarity.COMMON && stack.maxStackSize == 1) {
                realRarity = BountyRarity.UNCOMMON
            }

            val poolEntry = PoolEntry.create().apply {
                content = stack.id.toString()
                amount = PoolEntry.EntryRange(1, realAmtMax)
                unitWorth = realCost
                rarity = realRarity
                id = UUID.randomUUID().toString()
            }

            pool.items.add(poolEntry)
        }

        pool.setup(poolId)

        val decree = Decree(poolId, mutableSetOf(poolId), mutableSetOf(poolId))
        BountifulContent.Decrees.add(decree)
        BountifulContent.populatePools(listOf(pool))
    }

}