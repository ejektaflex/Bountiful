package ejektaflex.bountiful

import net.minecraftforge.common.ForgeConfigSpec
import org.apache.commons.lang3.tuple.Pair as ApachePair

class BountifulConfig {



    companion object {

        val specPair: ApachePair<Server, ForgeConfigSpec> = ForgeConfigSpec
                .Builder()
                .configure<Server> {
                    Server(it)
                }

        val serverSpec = specPair.right
        val SERVER: Server = specPair.left

    }

    class Server(b: ForgeConfigSpec.Builder) {
        var maxBountiesPerBoard: ForgeConfigSpec.IntValue = b.comment(
                "The maximum number of bounties present at a given board " +
                        "before it must delete some bounties to make room for more."
        ).defineInRange("maxBountiesPerBoard", 15, 1, 21)

        var boardAddFrequency: ForgeConfigSpec.IntValue = b
                .comment("How long, in seconds, between adding bounties to the bounty board")
                .defineInRange("boardAddFrequency", 90, 1, 100000)

    }

    var boardLifespan: Int = 72000
        private set
    var timeMultiplier: Double = 7.5
        private set
    var cashInAtBountyBoard: Boolean = true
        private set
    var rarityChance: Double = 0.4
        private set
    var bountyTimeMin = 6000
        private set
    var shouldCountdownOnBoard = false
        private set
    var bountiesCreatedOnPlace = 0
        private set
    var globalBounties = false
        private set
    var entityTimeMult = 2.0
        private set
    var rarityMultipliers = mutableListOf(1.0, 1.1, 1.2, 1.5)
        private set
    var boardRecipeEnabled: Boolean = false
        private set
    var bountyBoardBreakable: Boolean = true
        private set
    var greedyRewards: Boolean = false
        private set
    var villageGeneration: Boolean = true
        private set
    var randomBounties: Boolean = true
        private set
    var boardDrops: Boolean = true
        private set
    var xpBonuses = listOf(4, 10, 15, 25)
        private set
    var debugMode = true

    var namespaceBlacklist: MutableList<String> = mutableListOf()


}