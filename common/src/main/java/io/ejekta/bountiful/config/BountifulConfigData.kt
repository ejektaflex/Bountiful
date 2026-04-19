package io.ejekta.bountiful.config

import io.ejekta.bountiful.chaos.ChaosMode
import io.ejekta.bountiful.data.PoolEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class BountifulConfigData {

    @Serializable
    class BoardConfigData {
        var updateFrequencySecs: Int = 45
        var villageGenFrequency: Int = 2
        var canBreak = true
        var globalBoardState = false
    }

    val board = BoardConfigData()

    @Serializable
    class BountyConfigData {
        var flatBonusTimePerBountyInSecs: Int = 0
        var shouldHaveTimersAndExpire = true
        var fillerDifficultyModifierPercent = 0
        var allowDecreeMixing = true
        var reverseMatchingAlgorithm = false
        var initialCountPreference = PoolEntry.EntryRange(1, 2)
        var fillerCountPreference = PoolEntry.EntryRange(1, 2)
        var fillerCurrencyPool: String? = null
    }

    val bounty = BountyConfigData()

    @Serializable
    class ClientConfigData {
        var showCompletionToast = true
    }

    val client = ClientConfigData()

    @Serializable
    class GeneralConfigData {
        var dataPathsToExclude = listOf(
            "bounty_pools/bountiful/example_pool",
            "bounty_pools/*/another_example",
            "bounty_decrees/other/*"
        )
    }

    @Serializable
    class DebugConfigData {
        var enabled = false
    }

    val dbg = DebugConfigData()

    @Transient var chaosMode: ChaosMode? = if (dbg.enabled) {
        ChaosMode()
    } else null

    val general = GeneralConfigData()

}
