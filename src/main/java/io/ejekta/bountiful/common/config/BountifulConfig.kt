package io.ejekta.bountiful.common.config

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.serial.Format
import kotlinx.serialization.Serializable
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.LiteralText

@Serializable
class BountifulConfig {

    // select * from f_format_names('2020'); => ID, Name

    var boardUpdateFrequency: Int = 45
    var bonusTime: Int = 0

    fun buildScreen(): Screen {
        val builder = ConfigBuilder.create()
            .setParentScreen(MinecraftClient.getInstance().currentScreen)
            .setTitle(LiteralText("Bountiful"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val board = builder.getOrCreateCategory(LiteralText("General"))

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
                bonusTime,
                0, 6000
            ).setDefaultValue(45).setTooltip(
                LiteralText("How much bonus time is given to bounties")
            ).setSaveConsumer {
                bonusTime = it
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