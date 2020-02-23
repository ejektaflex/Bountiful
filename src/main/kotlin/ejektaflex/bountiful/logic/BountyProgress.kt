package ejektaflex.bountiful.logic

class BountyProgress(val pair: Pair<Int, Int>) {

    val isFinished: Boolean
        get() = pair.first == pair.second

    val stringNums: String
        get() = "(${pair.first}/${pair.second})"

    val color: String
        get() = if (isFinished) "§a" else "§c"

}