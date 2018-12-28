package ejektaflex.bountiful.api.logic

/**
 * An enum class representing all needed NBT variables on an item bounty
 */
enum class BountyNBT(var key: String) {
    BoardTime("boardTime"),
    BountyTime("bountyTime"),
    Rarity("rarity"),
    Worth("worth"),
    TimeStamp("timestamp"),
    ToGet("gets"),
    Rewards("rewards");
}