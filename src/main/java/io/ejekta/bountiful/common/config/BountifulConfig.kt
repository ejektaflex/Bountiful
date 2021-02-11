package io.ejekta.bountiful.common.config

import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText

@Serializable
class BountifulConfig {

    // select * from f_format_names('2020'); => ID, Name

    var boardUpdateFrequency: Int = 45
    var bountyBonusTime: Int = 0
    var bountyExpiryTimers = true


    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(LiteralText("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val board = builder.getOrCreateCategory(LiteralText("General - Board"))

        board.addEntry(
            creator.startIntSlider(
                LiteralText("Board Update Frequency"),
                boardUpdateFrequency,
                1, 600
            ).setDefaultValue(45).setTooltip(
                LiteralText("How often (in seconds) new bounties are added/removed")
            ).setSaveConsumer {
                boardUpdateFrequency = it
            }.build()
        )

        board.addEntry(
            creator.startIntSlider(
                LiteralText("Bonus Time"),
                bountyBonusTime,
                0, 6000
            ).setDefaultValue(45).setTooltip(
                LiteralText("How much bonus time is given to bounties")
            ).setSaveConsumer {
                bountyBonusTime = it
            }.build()
        )

        val bounty = builder.getOrCreateCategory(LiteralText("General - Bounty"))

        bounty.addEntry(
            creator.startBooleanToggle(
                LiteralText("Expiry Timers"),
                bountyExpiryTimers
            ).setDefaultValue(true).setTooltip(
                LiteralText("Whether bounties should have a time and expire")
            ).setSaveConsumer {
                bountyExpiryTimers = it
            }.build()
        )


        return builder.build()
    }

    fun onSave() {
        BountifulIO.saveConfig()
        BountifulIO.loadConfig()
    }

    companion object {

    }

}