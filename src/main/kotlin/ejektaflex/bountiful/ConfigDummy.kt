package ejektaflex.bountiful

import ejektaflex.bountiful.api.config.IBountifulConfig

class ConfigDummy : IBountifulConfig {
    override val bountyAmountRange: IntRange
        get() = 1..2
    override var maxBountiesPerBoard: Int = 17
        private set
    override var boardAddFrequency: Long = 2400L
        private set
    override var boardLifespan: Int = 72000
        private set
    override var timeMultiplier: Double = 28.0
        private set
    override var cashInAtBountyBoard: Boolean = true
        private set
    override var rarityChance: Double = 0.27
        private set
    override var bountyTimeMin = 6000
        private set
    override var shouldCountdownOnBoard = false
        private set
    override var bountiesCreatedOnPlace = 0
        private set
    override var globalBounties = false
        private set
    override var entityTimeMult = 2.0
        private set
    override var rarityMultipliers = mutableListOf(1.0, 1.1, 1.2, 1.5)
        private set
    override var boardRecipeEnabled: Boolean = false
        private set
    override var bountyBoardBreakable: Boolean = true
        private set
    override var greedyRewards: Boolean = false
        private set
    override var villageGeneration: Boolean = true
        private set
    override var randomBounties: Boolean = true
        private set
    override var boardDrops: Boolean = true
        private set
    override var xpBonuses = listOf(4, 10, 15, 25)
        private set
    override var compatGameStages: Boolean = true
        private set

    override fun load() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val isRunningGameStages: Boolean
        get() = false

}