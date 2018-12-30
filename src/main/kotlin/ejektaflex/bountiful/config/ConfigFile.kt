package ejektaflex.bountiful.config

import ejektaflex.bountiful.api.config.IBountifulConfig
import ejektaflex.bountiful.api.ext.clampTo
import java.io.File
import kotlin.math.max



data class ConfigFile(val folder: File) : KConfig(folder, "bountiful.cfg"), IBountifulConfig {

    override var maxBountiesPerBoard: Int = 12
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
    override var messageOnBountyMobDeath: Boolean = false
        private set


    private var bountyAmountMax = 2
    private var bountyAmountMin = 1

    override val bountyAmountRange: IntRange
        get() = bountyAmountMin..bountyAmountMax

    override fun load() {

        maxBountiesPerBoard = config.get(
                CATEGORY_BOARD,
                "Max Bounties Per Board At A Time",
                17,
                "How many entries should be on a bounty board at a given bountyTime. (Max: 27, Default: 17)"
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
                "The maximum number of items that a bounty could ask for (Default: 2)"
        ).int.clampTo(1..64)

        bountyAmountMin = config.get(
                CATEGORY_BOUNTY,
                "Bounty Items Min",
                1,
                "The minimum number of items that a bounty could ask for (Default: 1)"
        ).int.clampTo(1..64)

        bountyTimeMin = config.get(
                CATEGORY_BOUNTY,
                "Minimum Bounty Time",
                6000,
                "The minimum bountyTime, in ticks, that a bounty can take to complete (Default: 4800)"
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
                "The number of entries that a Bounty Board starts with when placed (Default: 0)"
        ).int.clampTo(1..27)

        messageOnBountyMobDeath = config.get(
                CATEGORY_BOUNTY,
                "Should Bounty Kills Notify Player?",
                false,
                "By default (false), killing a mob and having a bounty for the mob in your inventory will not notify you."
        ).boolean

    }

    companion object {
        private const val CATEGORY_BOARD = "board"
        private const val CATEGORY_BOUNTY = "bounty"
    }

}
