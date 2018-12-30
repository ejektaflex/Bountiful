package ejektaflex.bountiful.api.config

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
    val messageOnBountyMobDeath: Boolean
    fun load()
}