package ejektaflex.bountiful.api.logic

/**
 * An enum class representing all needed NBT variables on an item bounty
 */
enum class BountyNBT(var key: String) {
    BoardTime("boardTime"),
    BountyTime("bountyTime"),
    Rarity("rarity"),
    Worth("worth"),
    TickDown("tickdown"),
    ToGet("gets"),
    Rewards("rewards");
}