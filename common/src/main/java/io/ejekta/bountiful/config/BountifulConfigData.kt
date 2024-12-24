package io.ejekta.bountiful.config

import io.ejekta.bountiful.chaos.ChaosMode
import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.text.textLiteral
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

@Serializable
class BountifulConfigData {

    @Serializable
    class BoardConfigData {
        var updateFrequencySecs: Int = 45
        var villageGenFrequency: Int = 2
        var canBreak = true
    }

    val board = BoardConfigData()

    @Serializable
    class BountyConfigData {
        var flatBonusTimePerBountyInSecs: Int = 0
        var shouldHaveTimersAndExpire = true
        var objectiveDifficultyModifierPercent = 0
        var allowDecreeMixing = true
        var maxNumRewards = 2
        var reverseMatchingAlgorithm = false
        var matchCountPreference = PoolEntry.EntryRange(1, 2)
    }

    val bounty = BountyConfigData()

    @Serializable
    class ClientConfigData {
        var showCompletionToast = true
        var advancedDebugTooltips = false
    }

    val client = ClientConfigData()

    @Serializable
    class GeneralConfigData {
        var dataPackExclusions = listOf(
            "bounty_pools/bountiful/example_pool",
            "bounty_pools/*/another_example",
            "bounty_decrees/other/*"
        )
    }

    @Serializable
    class ChaosConfigData {
        var enabled = false
    }

    val chaos = ChaosConfigData()

    @Transient var chaosMode: ChaosMode? = if (chaos.enabled) {
        ChaosMode()
    } else null

    val general = GeneralConfigData()

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(Minecraft.getInstance().screen)
            .setTitle(Component.literal("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val generalCat = builder.getOrCreateCategory(Component.literal("General"))

        generalCat.addEntry(
            creator.startStrList(
                Component.literal("Excluded data paths"),
                general.dataPackExclusions
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                Component.literal("A list of data paths that should be excluded from loading")
            ).setSaveConsumer {
                general.dataPackExclusions = it
            }.setAddButtonTooltip(Component.literal("Adds a new exclusion rule. "))
                .build()
        )

        generalCat.addEntry(
            creator.startIntSlider(
                textLiteral("Board Gen Frequency in Villages"),
                board.villageGenFrequency,
                0, 32
            ).setDefaultValue(board.villageGenFrequency)
                .setTooltip(
                    textLiteral("How often bounty boards replace houses in villages")
                )
                .setSaveConsumer {
                    board.villageGenFrequency = it
                }
                .requireRestart()
                .build()
        )


        val boardCat = builder.getOrCreateCategory(Component.literal("General - Board"))

        boardCat.addEntry(
            creator.startIntField(
                textLiteral("Board Update Frequency"),
                board.updateFrequencySecs
            ).setDefaultValue(45).setTooltip(
                Component.literal("How often (in seconds) new bounties are added/removed")
            ).setSaveConsumer {
                board.updateFrequencySecs = it
            }.build()
        )

        val bountyCat = builder.getOrCreateCategory(Component.literal("General - Bounty"))

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Expiry Timers"),
                bounty.shouldHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                Component.literal("Whether bounties should have a timer and expire")
            ).setSaveConsumer {
                bounty.shouldHaveTimersAndExpire = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Breakable Boards"),
                board.canBreak
            ).setDefaultValue(true).setTooltip(
                Component.literal("Whether boards should be breakable or not")
            ).setSaveConsumer {
                board.canBreak = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Component.literal("Objective Requirement Multiplier"),
                bounty.objectiveDifficultyModifierPercent,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                Component.literal("Makes new bounties this percent more/less expensive, objective-wise")
            ).setSaveConsumer {
                bounty.objectiveDifficultyModifierPercent = it
            }.setTextGetter {
                textLiteral("$it% Change")
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Allow Decree Mixing"),
                bounty.allowDecreeMixing
            ).setDefaultValue(true).setTooltip(
                Component.literal("Whether all board decrees are considered when generating a bounty")
            ).setSaveConsumer {
                bounty.allowDecreeMixing = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Component.literal("Max Number of Rewards"),
                bounty.maxNumRewards,
                1, 4
            ).setDefaultValue(2).setTooltip(
                Component.literal("Determines the max number of rewards that will be in a bounty")
            ).setSaveConsumer {
                bounty.maxNumRewards = it
            }.setTextGetter {
                textLiteral("$it Rewards")
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Reverse Entry Matching Algorithm"),
                bounty.reverseMatchingAlgorithm
            ).setDefaultValue(false).setTooltip(
                Component.literal("Setting this to true reverses the generation algorithm")
            ).setSaveConsumer {
                bounty.reverseMatchingAlgorithm = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Component.literal("Bonus Time"),
                bounty.flatBonusTimePerBountyInSecs,
                0, 3600
            ).setDefaultValue(0).setTooltip(
                Component.literal("How much bonus time is given to bounties, in seconds")
            ).setSaveConsumer {
                bounty.flatBonusTimePerBountyInSecs = it
            }.build()
        )

        val clientCat = builder.getOrCreateCategory(Component.literal("Client"))

        clientCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Completion Toast Messages"),
                client.showCompletionToast
            ).setDefaultValue(true).setTooltip(
                Component.literal("Whether toast messages should appear upon bounty completion")
            ).setSaveConsumer {
                client.showCompletionToast = it
            }.build()
        )

        clientCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Advanced Debug Tooltips"),
                client.advancedDebugTooltips
            ).setDefaultValue(false).setTooltip(
                Component.literal("Whether advanced tooltips should show debug information")
            ).setSaveConsumer {
                client.advancedDebugTooltips = it
            }.build()
        )

        val chaosCat = builder.getOrCreateCategory(Component.literal("Chaos Mode"))

        chaosCat.addEntry(
            creator.startBooleanToggle(
                Component.literal("Enable Chaos Mode (Experimental)"),
                chaos.enabled
            ).setDefaultValue(false).setTooltip(
                Component.literal("Whether chaos mode is enabled. Will override all base and config data.")
            ).setSaveConsumer {
                chaos.enabled = it
                chaosMode = if (it) {
                    chaosMode ?: ChaosMode()
                } else {
                    null
                }
            }.build()
        )

        return builder.build()
    }

    private fun onSave() {
        BountifulIO.reloadConfig()
    }

}