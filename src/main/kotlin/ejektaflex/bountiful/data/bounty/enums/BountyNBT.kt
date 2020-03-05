package ejektaflex.bountiful.data.bounty.enums

/**
 * An enum class representing all needed NBT variables on an item bounty
 */
enum class BountyNBT(var key: String) {
    BoardStamp("boardStamp"),
    BountyTime("bountyTime"),
    Rarity("rarity"),
    BountyStamp("bountyStamp"),
    Objectives("objectives"),
    Rewards("rewards");
}