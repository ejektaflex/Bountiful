package ejektaflex.bountiful.data.bounty.checkers

import net.minecraft.item.ItemStack

class StackPartition(val stack: ItemStack) {

    val size = stack.count
    var free = size

    val reserved: Int
        get() = size - free

    // Returns: The amount we could not reserve
    fun reserve(amt: Int): Int {
        return if (amt <= free) {
            free -= amt
            0
        } else { // amt > free
            val toRet = amt - free
            free = 0
            toRet
        }
    }

    override fun toString(): String {
        return "PartitionMap[f=$free,r=$reserved,s=$size]"
    }

    fun shrink() {
        stack.shrink(reserved)
    }

}