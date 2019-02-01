package ejektaflex.bountiful.config

import ejektaflex.bountiful.api.config.IBountifulConfig
import ejektaflex.bountiful.api.ext.clampTo
import net.minecraftforge.fml.common.Loader
import java.io.File
import kotlin.math.max



data class ConfigFile(val folder: File) : KConfig(folder, "bountiful.cfg"), IBountifulConfig {

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




    private var bountyAmountMax = 2
    private var bountyAmountMin = 1

    override val bountyAmountRange: IntRange
        get() = bountyAmountMin..bountyAmountMax

    val isRunningGameStages: Boolean
        get() = compatGameStages && Loader.isModLoaded("gamestages")

    override fun load() {

        maxBountiesPerBoard = config.get(
                CATEGORY_BOARD,
                "Max Bounties Per Board At A Time",
                17,
                "How many entries should be on a bounty board at a given time. (Max: 27, Default: 17)"
        ).int.clampTo(1..27)

        boardAddFrequency = max(config.get(
                CATEGORY_BOARD,
                "New Bounty Frequency",
                2400,
                "How often, in ticks, new bounty should show up on the bounty board. (Min: 20, Default: 2400)"
        ).int, 20).toLong().clampTo(20L..Long.MAX_VALUE)

        boardLifespan = config.get(
                CATEGORY_BOARD,
                "Bounty on Board Lifespan",
                72000,
                "How long entries stay on the board, at max (Bounties will be removed prematurely if board hits max entries). (Default: 72000)"
        ).int.clampTo(10..Int.MAX_VALUE)


        // Bounty

        timeMultiplier = config.get(
                CATEGORY_BOUNTY,
                "Bounty Expiry Time Multiplier",
                28.0,
                "A general multiplier for how long you get to complete a bounty, based on bounty worth. (Default: 28.0)"
        ).double.clampTo(0.0, Double.MAX_VALUE)

        cashInAtBountyBoard = config.get(
                CATEGORY_BOUNTY,
                "Cash In At Bounty Board?",
                true,
                "By default (true), the user must cash in the bounty by right clicking on the bounty board. If false, you can right click with the item in hand."
        ).boolean

        rarityChance = config.get(
                CATEGORY_BOUNTY,
                "Rarity Increase Chance",
                0.27,
                "The chance, per level, for a bounty to increase in rarity (Default: 0.27)"
        ).double.clampTo(0.0, 1.0)

        bountyAmountMax = config.get(
                CATEGORY_BOUNTY,
                "Bounty Items Max",
                2,
                "The maximum number of objectives that a bounty could ask for (Default: 2)"
        ).int.clampTo(1..64)

        bountyAmountMin = config.get(
                CATEGORY_BOUNTY,
                "Bounty Items Min",
                1,
                "The minimum number of objectives that a bounty could ask for (Default: 1)"
        ).int.clampTo(1..64)

        bountyTimeMin = config.get(
                CATEGORY_BOUNTY,
                "Minimum Bounty Time",
                6000,
                "The minimum time, in ticks, required to complete a bounty. (Default: 6000)"
        ).int.clampTo(10..Int.MAX_VALUE)

        shouldCountdownOnBoard = config.get(
                CATEGORY_BOUNTY,
                "Should Bounties Count Down on Board?",
                false,
                "By default (false), entries do not start counting down until the player takes them."
        ).boolean

        bountiesCreatedOnPlace = config.get(
                CATEGORY_BOARD,
                "Bounties Created On Place",
                0,
                "The number of entries that a Bounty Board starts with when placed, if not using global bounties (Default: 0)"
        ).int.clampTo(0..27)

        globalBounties = config.get(
                CATEGORY_BOARD,
                "Global Bounty Inventory",
                false,
                "By default (false), all boards share a single, global inventory per dimension. If false, all boards have their own inventory."
        ).boolean

        entityTimeMult = config.get(
                CATEGORY_BOUNTY,
                "Entity Bounty Time Multiplier",
                2.0,
                "A multiplier for how much longer entity (mob) bounties will give you to complete than item bounties."
        ).double.clampTo(0.01, 100.0)

        boardRecipeEnabled = config.get(
                CATEGORY_BOARD,
                "Board Recipe Enabled?",
                false,
                "Whether or not a recipe for the bounty board is created (Default: false)."
        ).boolean

        bountyBoardBreakable = config.get(
                CATEGORY_BOARD,
                "Board Breakable?",
                true,
                "Whether or not the bounty board can be broken (Default: true)."
        ).boolean

        greedyRewards = config.get(
                CATEGORY_REWARDS,
                "Greedy Rewards?",
                false,
                "If using a currency for rewards, set this to true. By default (false), rewards will be picked at random until they match the bounty value (adjusted by rarity). If true, rewards will be greedily chosen (The most expensive coming first) until they match the bounty value. Currency rewards benefit from setting this to true because the highest possible coin values will be given first. With this turned on, reward weights are ignored."
        ).boolean

        villageGeneration = config.get(
                CATEGORY_BOARD,
                "Village Generation",
                true,
                "Whether or not bounty boards naturally generate in villages (Default: true)."
        ).boolean

        boardDrops = config.get(
                CATEGORY_BOARD,
                "Board Drops on Break",
                true,
                "Whether or not bounty boards will drop when broken (Default: true)."
        ).boolean

        xpBonuses = config.get(
                CATEGORY_MISC,
                "How much experience each rarity of bounty should give you.",
                listOf(4, 10, 15, 25).toTypedArray().toIntArray(),
                "A multiplier for how much a common bounty is worth. (Default: 5 (Common), 10 (Uncommon), 15 (Rare), 25 (Epic))"
        ).intList.toList()

        compatGameStages = config.get(
                CATEGORY_COMPAT,
                "GameStages Compat",
                true,
                "Whether or not gamestages compat is enabled."
        ).boolean


        /*
        randomBounties = config.get(
                CATEGORY_BOUNTY,
                "Random Bounties",
                true,
                "By default (true), bounties are randomly created based on 'bounties.json'. If set to false, premade bounties will instead be picked from 'premade.json'."
        ).boolean
        */



        rarityMultipliers[0] = config.get(
                CATEGORY_RARITY,
                "a) Common Worth Multiplier",
                1.0,
                "A multiplier for how much a common bounty is worth. (Default: 1.0)"
        ).double.clampTo(0.01, Double.MAX_VALUE)

        rarityMultipliers[1] = config.get(
                CATEGORY_RARITY,
                "b) Uncommon Worth Multiplier",
                1.1,
                "A multiplier for how much an uncommon bounty is worth (Default: 1.1)"
        ).double.clampTo(0.01, Double.MAX_VALUE)

        rarityMultipliers[2] = config.get(
                CATEGORY_RARITY,
                "c) Rare Worth Multiplier",
                1.2,
                "A multiplier for how much a rare bounty is worth (Default: 1.2)"
        ).double.clampTo(0.01, Double.MAX_VALUE)

        rarityMultipliers[3] = config.get(
                CATEGORY_RARITY,
                "d) Epic Worth Multiplier",
                1.5,
                "A multiplier for how much an epic bounty is worth (Default: 1.5)"
        ).double.clampTo(0.01, Double.MAX_VALUE)

    }

    companion object {
        private const val CATEGORY_BOARD = "board"
        private const val CATEGORY_BOUNTY = "bounty"
        private const val CATEGORY_RARITY = "rarity"
        private const val CATEGORY_REWARDS = "rewards"
        private const val CATEGORY_MISC = "misc"
        private const val CATEGORY_COMPAT = "compat"
    }

}
