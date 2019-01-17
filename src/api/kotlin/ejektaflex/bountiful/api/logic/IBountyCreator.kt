package ejektaflex.bountiful.api.logic

import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

interface IBountyCreator {
    /**
     * Creates a new, randomized bounty
     */
    fun createStack(world: World): ItemStack

    /**
     * Generates a random rarity according to the Bountiful config
     */
    fun calcRarity(): EnumBountyRarity

    /**
     * Generates new, randomized bounty data
     */
    fun create(inRarity: EnumBountyRarity? = null): IBountyData

}