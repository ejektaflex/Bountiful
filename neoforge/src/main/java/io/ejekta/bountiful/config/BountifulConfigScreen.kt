package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.chaos.ChaosMode
import io.ejekta.bountiful.data.PoolEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object BountifulConfigScreen {

    fun buildScreen(): Screen {
        fun tr(path: String, vararg args: Any): Component = Component.translatable("bountiful.config.$path", *args)

        val config = BountifulIO.configData

        val builder = ConfigBuilder.create()
            .setParentScreen(Minecraft.getInstance().screen)
            .setTitle(Component.translatable("bountiful.mod.name"))
            .setSavingRunnable(BountifulIO::reloadConfig)

        val creator = builder.entryBuilder()

        val generalCat = builder.getOrCreateCategory(tr("category.general"))

        generalCat.addEntry(
            creator.startStrList(
                tr("general.excluded_data_paths"),
                config.general.dataPathsToExclude
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                tr("general.excluded_data_paths.tooltip")
            ).setSaveConsumer {
                config.general.dataPathsToExclude = it
            }.setAddButtonTooltip(tr("general.excluded_data_paths.add_tooltip"))
                .build()
        )

        val boardCat = builder.getOrCreateCategory(tr("category.general_board"))

        boardCat.addEntry(
            creator.startBooleanToggle(
                tr("board.breakable_boards"),
                config.board.canBreak
            ).setDefaultValue(true).setTooltip(
                tr("board.breakable_boards.tooltip")
            ).setSaveConsumer {
                config.board.canBreak = it
            }.build()
        )

        boardCat.addEntry(
            creator.startIntField(
                tr("board.update_frequency"),
                config.board.updateFrequencySecs
            ).setDefaultValue(45).setTooltip(
                tr("board.update_frequency.tooltip")
            ).setSaveConsumer {
                config.board.updateFrequencySecs = it
            }.build()
        )

        boardCat.addEntry(
            creator.startFloatField(
                tr("board.village_gen_frequency"),
                config.board.villageGenFrequency
            ).setDefaultValue(1.0f)
                .setMin(0.0f)
                .setTooltip(tr("board.village_gen_frequency.tooltip"))
                .setSaveConsumer { config.board.villageGenFrequency = it }
                .requireRestart()
                .build()
        )

        val bountyCat = builder.getOrCreateCategory(tr("category.general_bounty"))

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.expiry_timers"),
                config.bounty.shouldHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                tr("bounty.expiry_timers.tooltip")
            ).setSaveConsumer {
                config.bounty.shouldHaveTimersAndExpire = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.objective_requirement_multiplier"),
                config.bounty.fillerDifficultyModifierPercent,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                tr("bounty.objective_requirement_multiplier.tooltip")
            ).setSaveConsumer {
                config.bounty.fillerDifficultyModifierPercent = it
            }.setTextGetter {
                tr("bounty.objective_requirement_multiplier.value", it)
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.allow_decree_mixing"),
                config.bounty.allowDecreeMixing
            ).setDefaultValue(true).setTooltip(
                tr("bounty.allow_decree_mixing.tooltip")
            ).setSaveConsumer {
                config.bounty.allowDecreeMixing = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.max_number_of_rewards"),
                config.bounty.initialCountPreference.max,
                1, 4
            ).setDefaultValue(2).setTooltip(
                tr("bounty.max_number_of_rewards.tooltip")
            ).setSaveConsumer {
                config.bounty.initialCountPreference = PoolEntry.EntryRange(config.bounty.initialCountPreference.min, it)
            }.setTextGetter {
                tr("bounty.max_number_of_rewards.value", it)
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.reverse_entry_matching_algorithm"),
                config.bounty.reverseMatchingAlgorithm
            ).setDefaultValue(false).setTooltip(
                tr("bounty.reverse_entry_matching_algorithm.tooltip")
            ).setSaveConsumer {
                config.bounty.reverseMatchingAlgorithm = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.bonus_time"),
                config.bounty.flatBonusTimePerBountyInSecs,
                0, 3600
            ).setDefaultValue(0).setTooltip(
                tr("bounty.bonus_time.tooltip")
            ).setSaveConsumer {
                config.bounty.flatBonusTimePerBountyInSecs = it
            }.build()
        )

        val clientCat = builder.getOrCreateCategory(tr("category.client"))

        clientCat.addEntry(
            creator.startBooleanToggle(
                tr("client.completion_toast_messages"),
                config.client.showCompletionToast
            ).setDefaultValue(true).setTooltip(
                tr("client.completion_toast_messages.tooltip")
            ).setSaveConsumer {
                config.client.showCompletionToast = it
            }.build()
        )

        val debugCat = builder.getOrCreateCategory(tr("category.debug"))

        debugCat.addEntry(
            creator.startBooleanToggle(
                tr("debug.enable_chaos_mode"),
                config.dbg.enabled
            ).setDefaultValue(false).setTooltip(
                tr("debug.enable_chaos_mode.tooltip")
            ).setSaveConsumer {
                config.dbg.enabled = it
                config.chaosMode = if (it) {
                    config.chaosMode ?: ChaosMode()
                } else {
                    null
                }
            }.build()
        )

        debugCat.addEntry(
            creator.startBooleanToggle(
                tr("debug.pack_mode"),
                Bountiful.packMode
            ).setDefaultValue(false).setTooltip(
                tr("debug.pack_mode.tooltip")
            ).setSaveConsumer {
                Bountiful.packMode = it
            }.build()
        )

        return builder.build()
    }
}
