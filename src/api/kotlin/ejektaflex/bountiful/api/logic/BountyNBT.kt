package ejektaflex.bountiful.api.logic

/**
 * An enum class representing all needed NBT variables on an item bounty
 */
enum class BountyNBT(var key: String) {
    BoardStamp("boardStamp"),
    BountyTime("bountyTime"),
    Rarity("rarity"),
    Worth("worth"),
    BountyStamp("bountyStamp"),
    ToGet("gets"),
    Rewards("rewards");
}