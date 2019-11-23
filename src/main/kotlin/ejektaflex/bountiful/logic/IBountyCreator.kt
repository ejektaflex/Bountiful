package ejektaflex.bountiful.logic


import ejektaflex.bountiful.api.data.IBountyData
import ejektaflex.bountiful.api.enum.EnumBountyRarity
import net.minecraft.item.ItemStack
import net.minecraft.world.World

interface IBountyCreator {
    /**
     * Creates a new, randomized bounty. If there are no valid rewards available, returns null instead.
     */
    fun createStack(world: World, rarity: EnumBountyRarity? = null): ItemStack?

    /**
     * Generates a random rarity according to the Bountiful config
     */
    fun calcRarity(): EnumBountyRarity

    /**
     * Generates new, randomized bounty data
     */
    fun create(world: World, inRarity: EnumBountyRarity? = null): IBountyData?

}