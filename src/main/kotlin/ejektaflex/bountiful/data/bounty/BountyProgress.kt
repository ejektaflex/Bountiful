package ejektaflex.bountiful.data.bounty

import net.minecraft.ChatFormatting

class BountyProgress(val pair: Pair<Int, Int>) {

    val isFinished: Boolean
        get() = pair.first == pair.second

    val stringNums: String
        get() = "(${pair.first}/${pair.second})"

    val color: ChatFormatting
        get() = if (isFinished) ChatFormatting.GREEN else ChatFormatting.RED

}