package ejektaflex.bountiful.data

import net.minecraft.item.ItemStack

// Currently unused, but if there would ever be a reason that tooltip code was causing performance
// issues then we could start caching the tooltip here.
object BountyTooltipCache : ICache<ItemStack, MutableList<String>> {

    // A cache of
    private val cache = mutableMapOf<ItemStack, MutableList<String>>()

    override fun contains(item: ItemStack): Boolean {
        return item in cache.keys
    }

    override fun store(item: ItemStack, data: MutableList<String>) {
        cache[item] = data
    }

    override fun invalidate(item: ItemStack) {
        if (item in this) {
            cache.remove(item)
        }
    }

}