package ejektaflex.bountiful.data.bounty

import net.minecraft.util.text.TextFormatting

class BountyProgress(val pair: Pair<Int, Int>) {

    val isFinished: Boolean
        get() = pair.first == pair.second

    val stringNums: String
        get() = "(${pair.first}/${pair.second})"

    val color: TextFormatting
        get() = if (isFinished) TextFormatting.GREEN else TextFormatting.RED

}