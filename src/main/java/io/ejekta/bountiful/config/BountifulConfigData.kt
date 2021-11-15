package io.ejekta.bountiful.config

import io.ejekta.kambrik.text.textLiteral
import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText

@Serializable
class BountifulConfigData {

    var boardUpdateFrequency: Int = 45
    var boardGenFrequency: Int = 2
    var flatBonusTimePerBounty: Int = 0
    var shouldBountiesHaveTimersAndExpire = true
    var dataPackExclusions = mutableListOf(
        "bounty_pools/bountiful/something_here"
    )
    var objectiveModifier = 0


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(LiteralText("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val general = builder.getOrCreateCategory(LiteralText("General"))

        general.addEntry(
            creator.startStrList(
                LiteralText("Excluded data paths"),
                dataPackExclusions
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                LiteralText("A list of data paths that should be excluded from loading")
            ).setSaveConsumer {
                dataPackExclusions = it
            }.setAddButtonTooltip(LiteralText("Adds a new exclusion rule. "))
                .build()
        )

        general.addEntry(
            creator.startIntSlider(
                textLiteral("Board generation frequency in villages"),
                boardGenFrequency,
                0, 32
            ).setDefaultValue(boardGenFrequency)
                .setTooltip(
                    textLiteral("How often bounty boards generate in villages")
                )
                .setSaveConsumer {
                    boardGenFrequency = it
                }
                .requireRestart()
                .build()
        )


        val board = builder.getOrCreateCategory(LiteralText("General - Board"))

        board.addEntry(
            creator.startIntField(
                textLiteral("Board Update Frequency"),
                boardUpdateFrequency
            ).setDefaultValue(45).setTooltip(
                LiteralText("How often (in seconds) new bounties are added/removed")
            ).setSaveConsumer {
                boardUpdateFrequency = it
            }.build()
        )

        board.addEntry(
            creator.startIntSlider(
                LiteralText("Bonus Time"),
                flatBonusTimePerBounty,
                0, 6000
            ).setDefaultValue(0).setTooltip(
                LiteralText("How much bonus time is given to bounties")
            ).setSaveConsumer {
                flatBonusTimePerBounty = it
            }.build()
        )

        val bounty = builder.getOrCreateCategory(LiteralText("General - Bounty"))

        bounty.addEntry(
            creator.startBooleanToggle(
                LiteralText("Expiry Timers"),
                shouldBountiesHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                LiteralText("Whether bounties should have a timer and expire")
            ).setSaveConsumer {
                shouldBountiesHaveTimersAndExpire = it
            }.build()
        )

        bounty.addEntry(
            creator.startIntSlider(
                LiteralText("Objective Requirement Multiplier"),
                objectiveModifier,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                LiteralText("Makes bounties this percent more/less expensive")
            ).setSaveConsumer {
                objectiveModifier = it
            }.setTextGetter {
                textLiteral("$it% Change") 
            }
                .build()
        )

        return builder.build()
    }

    private fun onSave() {
        BountifulIO.saveConfig()
        BountifulIO.loadConfig()
    }

}