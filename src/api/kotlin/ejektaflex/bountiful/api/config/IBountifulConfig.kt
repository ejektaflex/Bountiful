package ejektaflex.bountiful.api.config

/**
 * Explanations of each config option are generated in the config implementation, so are they withheld here.
 */
interface IBountifulConfig {
    val maxBountiesPerBoard: Int
    val boardAddFrequency: Long
    val boardLifespan: Int
    val timeMultiplier: Double
    val cashInAtBountyBoard: Boolean
    val rarityChance: Double
    val bountyTimeMin: Int
    val bountyAmountRange: IntRange
    val shouldCountdownOnBoard: Boolean
    val bountiesCreatedOnPlace: Int
    val globalBounties: Boolean
    val entityTimeMult: Double
    val rarityMultipliers: List<Double>
    val boardRecipeEnabled: Boolean
    val bountyBoardBreakable: Boolean
    val greedyRewards: Boolean
    val villageGeneration: Boolean
    val randomBounties: Boolean
    val boardDrops: Boolean
    fun load()
}