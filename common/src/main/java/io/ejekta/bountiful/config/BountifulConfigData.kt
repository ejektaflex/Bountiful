package io.ejekta.bountiful.config

import io.ejekta.bountiful.data.PoolEntry
import io.ejekta.kambrik.text.textLiteral
import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

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

    val general = GeneralConfigData()

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(Text.literal("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val generalCat = builder.getOrCreateCategory(Text.literal("General"))

        generalCat.addEntry(
            creator.startStrList(
                Text.literal("Excluded data paths"),
                general.dataPackExclusions
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                Text.literal("A list of data paths that should be excluded from loading")
            ).setSaveConsumer {
                general.dataPackExclusions = it
            }.setAddButtonTooltip(Text.literal("Adds a new exclusion rule. "))
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


        val boardCat = builder.getOrCreateCategory(Text.literal("General - Board"))

        boardCat.addEntry(
            creator.startIntField(
                textLiteral("Board Update Frequency"),
                board.updateFrequencySecs
            ).setDefaultValue(45).setTooltip(
                Text.literal("How often (in seconds) new bounties are added/removed")
            ).setSaveConsumer {
                board.updateFrequencySecs = it
            }.build()
        )

        val bountyCat = builder.getOrCreateCategory(Text.literal("General - Bounty"))

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Expiry Timers"),
                bounty.shouldHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether bounties should have a timer and expire")
            ).setSaveConsumer {
                bounty.shouldHaveTimersAndExpire = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Breakable Boards"),
                board.canBreak
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether boards should be breakable or not")
            ).setSaveConsumer {
                board.canBreak = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Text.literal("Objective Requirement Multiplier"),
                bounty.objectiveDifficultyModifierPercent,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                Text.literal("Makes new bounties this percent more/less expensive, objective-wise")
            ).setSaveConsumer {
                bounty.objectiveDifficultyModifierPercent = it
            }.setTextGetter {
                textLiteral("$it% Change")
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Allow Decree Mixing"),
                bounty.allowDecreeMixing
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether all board decrees are considered when generating a bounty")
            ).setSaveConsumer {
                bounty.allowDecreeMixing = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Text.literal("Max Number of Rewards"),
                bounty.maxNumRewards,
                1, 4
            ).setDefaultValue(2).setTooltip(
                Text.literal("Determines the max number of rewards that will be in a bounty")
            ).setSaveConsumer {
                bounty.maxNumRewards = it
            }.setTextGetter {
                textLiteral("$it Rewards")
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Reverse Entry Matching Algorithm"),
                bounty.reverseMatchingAlgorithm
            ).setDefaultValue(false).setTooltip(
                Text.literal("Setting this to true reverses the generation algorithm")
            ).setSaveConsumer {
                bounty.reverseMatchingAlgorithm = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                Text.literal("Bonus Time"),
                bounty.flatBonusTimePerBountyInSecs,
                0, 3600
            ).setDefaultValue(0).setTooltip(
                Text.literal("How much bonus time is given to bounties, in seconds")
            ).setSaveConsumer {
                bounty.flatBonusTimePerBountyInSecs = it
            }.build()
        )

        val clientCat = builder.getOrCreateCategory(Text.literal("Client"))

        clientCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Completion Toast Messages"),
                client.showCompletionToast
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether toast messages should appear upon bounty completion")
            ).setSaveConsumer {
                client.showCompletionToast = it
            }.build()
        )

        clientCat.addEntry(
            creator.startBooleanToggle(
                Text.literal("Advanced Debug Tooltips"),
                client.advancedDebugTooltips
            ).setDefaultValue(false).setTooltip(
                Text.literal("Whether advanced tooltips should show debug information")
            ).setSaveConsumer {
                client.advancedDebugTooltips = it
            }.build()
        )

        return builder.build()
    }

    private fun onSave() {
        BountifulIO.reloadConfig()
    }

}