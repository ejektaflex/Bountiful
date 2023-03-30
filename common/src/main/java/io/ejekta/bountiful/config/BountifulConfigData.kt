package io.ejekta.bountiful.config

import io.ejekta.kambrik.text.textLiteral
import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

@Serializable
class BountifulConfigData {

    var boardUpdateFrequency: Int = 45
    var boardGenFrequency: Int = 2
    var flatBonusTimePerBounty: Int = 0
    var shouldBountiesHaveTimersAndExpire = true
    var dataPackExclusions = listOf(
        "bounty_pools/bountiful/example_pool",
        "bounty_pools/*/another_example",
        "bounty_decrees/other/*"
    )
    var objectiveModifier = 0
    var maxNumRewards = 2
    var showCompletionToast = true

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(Text.literal("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val general = builder.getOrCreateCategory(Text.literal("General"))

        general.addEntry(
            creator.startStrList(
                Text.literal("Excluded data paths"),
                dataPackExclusions
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                Text.literal("A list of data paths that should be excluded from loading")
            ).setSaveConsumer {
                dataPackExclusions = it
            }.setAddButtonTooltip(Text.literal("Adds a new exclusion rule. "))
                .build()
        )

        general.addEntry(
            creator.startIntSlider(
                textLiteral("Board Gen Frequency in Villages"),
                boardGenFrequency,
                0, 32
            ).setDefaultValue(boardGenFrequency)
                .setTooltip(
                    textLiteral("How often bounty boards replace houses in villages")
                )
                .setSaveConsumer {
                    boardGenFrequency = it
                }
                .requireRestart()
                .build()
        )


        val board = builder.getOrCreateCategory(Text.literal("General - Board"))

        board.addEntry(
            creator.startIntField(
                textLiteral("Board Update Frequency"),
                boardUpdateFrequency
            ).setDefaultValue(45).setTooltip(
                Text.literal("How often (in seconds) new bounties are added/removed")
            ).setSaveConsumer {
                boardUpdateFrequency = it
            }.build()
        )

        board.addEntry(
            creator.startIntSlider(
                Text.literal("Bonus Time"),
                flatBonusTimePerBounty,
                0, 6000
            ).setDefaultValue(0).setTooltip(
                Text.literal("How much bonus time is given to bounties")
            ).setSaveConsumer {
                flatBonusTimePerBounty = it
            }.build()
        )

        val bounty = builder.getOrCreateCategory(Text.literal("General - Bounty"))

        bounty.addEntry(
            creator.startBooleanToggle(
                Text.literal("Expiry Timers"),
                shouldBountiesHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether bounties should have a timer and expire")
            ).setSaveConsumer {
                shouldBountiesHaveTimersAndExpire = it
            }.build()
        )

        bounty.addEntry(
            creator.startIntSlider(
                Text.literal("Objective Requirement Multiplier"),
                objectiveModifier,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                Text.literal("Makes new bounties this percent more/less expensive, objective-wise")
            ).setSaveConsumer {
                objectiveModifier = it
            }.setTextGetter {
                textLiteral("$it% Change")
            }.build()
        )

        bounty.addEntry(
            creator.startIntSlider(
                Text.literal("Max Number of Rewards"),
                maxNumRewards,
                1, 4
            ).setDefaultValue(2).setTooltip(
                Text.literal("Determines the max number of rewards that will be in a bounty")
            ).setSaveConsumer {
                maxNumRewards = it
            }.setTextGetter {
                textLiteral("$it Rewards")
            }.build()
        )

        val client = builder.getOrCreateCategory(Text.literal("Client"))

        client.addEntry(
            creator.startBooleanToggle(
                Text.literal("Completion Toast Messages"),
                showCompletionToast
            ).setDefaultValue(true).setTooltip(
                Text.literal("Whether toast messages should appear upon bounty completion")
            ).setSaveConsumer {
                showCompletionToast = it
            }.build()
        )

        return builder.build()
    }

    private fun onSave() {
        BountifulIO.saveConfig()
        BountifulIO.loadConfig()
    }

}