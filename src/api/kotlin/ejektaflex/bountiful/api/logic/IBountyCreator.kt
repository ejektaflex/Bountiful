package ejektaflex.bountiful.api.logic

import ejektaflex.bountiful.api.enum.EnumBountyRarity
import net.minecraft.item.ItemStack

interface IBountyCreator {
    /**
     * Creates a new, randomized bounty
     */
    fun createStack(): ItemStack

    /**
     * Generates a random rarity according to the Bountiful config
     */
    fun calcRarity(): EnumBountyRarity

    /**
     * Generates new, randomized bounty data
     */
    fun create(): IBountyData

}