package ejektaflex.bountiful.api.config

interface IBountifulConfig {
    var maxBountiesPerBoard: Int
    var boardAddFrequency: Long
    var boardLifespan: Int
    var timeMultiplier: Double
    var cashInAtBountyBoard: Boolean
    var rarityChance: Double
    var bountyTimeMin: Int
    val bountyAmountRange: IntRange
    fun load()
}