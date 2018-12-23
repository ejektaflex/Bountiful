package bountiful.config

import java.io.File
import kotlin.math.max
import kotlin.math.min

data class ConfigFile(val folder: File) : KConfig(folder, "bountiful.cfg") {

    var maxBountiesPerBoard: Int = 12
    var boardAddFrequency: Long = 40L
    var boardLifespan: Int = 72000
    var timeMultiplier: Long = 14L
    var cashInAtBountyBoard: Boolean = true

    override fun load() {

        maxBountiesPerBoard = min(config.get(
                CATEGORY_GENERAL,
                "Max Bounties Per Board At A Time",
                12,
                "How many bounties should be on a bounty board at a given time. (Max: 27)"
        ).int, 27)

        boardAddFrequency = max(config.get(
                CATEGORY_GENERAL,
                "New Bounty Frequency",
                2400,
                "How often, in ticks, new bounty should show up on the bounty board. (Min: 20, Default: 2400)"
        ).int, 20).toLong()

        boardLifespan = config.get(
                CATEGORY_GENERAL,
                "Bounty on Board Lifespan",
                72000,
                "How long bounties stay on the board, at max (Bounties will be removed prematurely if board hits max bounties). (Default: 72000)"
        ).int

        timeMultiplier = config.get(
                CATEGORY_GENERAL,
                "Bounty Expiry Time Multiplier",
                14,
                "A general multiplier for how long you get to complete a bounty, based on bounty worth. (Default: 14)"
        ).int.toLong()

        cashInAtBountyBoard = config.get(
                CATEGORY_GENERAL,
                "Cash In At Bounty Board?",
                true,
                "By default (true), the user must cash in the bounty by right clicking on the bounty board. If false, you can right click with the item in hand."
        ).boolean


    }

    companion object {
        private const val CATEGORY_GENERAL = "general"
    }

}
