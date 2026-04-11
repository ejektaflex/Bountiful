package io.ejekta.bountiful.config

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.chaos.ChaosMode
import io.ejekta.bountiful.data.PoolEntry
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

    fun buildScreen(): Screen {
        fun tr(path: String, vararg args: Any): Component = Component.translatable("bountiful.config.$path", *args)

        val builder = ConfigBuilder.create()
            .setParentScreen(Minecraft.getInstance().screen)
            .setTitle(Component.translatable("bountiful.mod.name"))
            .setSavingRunnable(::onSave)

        val creator = builder.entryBuilder()

        val generalCat = builder.getOrCreateCategory(tr("category.general"))

        generalCat.addEntry(
            creator.startStrList(
                tr("general.excluded_data_paths"),
                general.dataPathsToExclude
            ).setDefaultValue {
                listOf(
                    "bounty_pools/bountiful/example_pool",
                    "bounty_pools/*/another_example",
                    "bounty_decrees/other/*"
                )
            }.setTooltip(
                tr("general.excluded_data_paths.tooltip")
            ).setSaveConsumer {
                general.dataPathsToExclude = it
            }.setAddButtonTooltip(tr("general.excluded_data_paths.add_tooltip"))
                .build()
        )

        val boardCat = builder.getOrCreateCategory(tr("category.general_board"))


        boardCat.addEntry(
            creator.startBooleanToggle(
                tr("board.breakable_boards"),
                board.canBreak
            ).setDefaultValue(true).setTooltip(
                tr("board.breakable_boards.tooltip")
            ).setSaveConsumer {
                board.canBreak = it
            }.build()
        )

        boardCat.addEntry(
            creator.startIntField(
                tr("board.update_frequency"),
                board.updateFrequencySecs
            ).setDefaultValue(45).setTooltip(
                tr("board.update_frequency.tooltip")
            ).setSaveConsumer {
                board.updateFrequencySecs = it
            }.build()
        )

        boardCat.addEntry(
            creator.startIntSlider(
                tr("board.village_gen_frequency"),
                board.villageGenFrequency,
                0, 32
            ).setDefaultValue(board.villageGenFrequency)
                .setTooltip(
                    tr("board.village_gen_frequency.tooltip")
                )
                .setSaveConsumer {
                    board.villageGenFrequency = it
                }
                .requireRestart()
                .build()
        )

        val bountyCat = builder.getOrCreateCategory(tr("category.general_bounty"))

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.expiry_timers"),
                bounty.shouldHaveTimersAndExpire
            ).setDefaultValue(true).setTooltip(
                tr("bounty.expiry_timers.tooltip")
            ).setSaveConsumer {
                bounty.shouldHaveTimersAndExpire = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.objective_requirement_multiplier"),
                bounty.fillerDifficultyModifierPercent,
                -50, 100
            ).setDefaultValue(0).setTooltip(
                tr("bounty.objective_requirement_multiplier.tooltip")
            ).setSaveConsumer {
                bounty.fillerDifficultyModifierPercent = it
            }.setTextGetter {
                tr("bounty.objective_requirement_multiplier.value", it)
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.allow_decree_mixing"),
                bounty.allowDecreeMixing
            ).setDefaultValue(true).setTooltip(
                tr("bounty.allow_decree_mixing.tooltip")
            ).setSaveConsumer {
                bounty.allowDecreeMixing = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.max_number_of_rewards"),
                bounty.initialCountPreference.max,
                1, 4
            ).setDefaultValue(2).setTooltip(
                tr("bounty.max_number_of_rewards.tooltip")
            ).setSaveConsumer {
                bounty.initialCountPreference = PoolEntry.EntryRange(bounty.initialCountPreference.min, it)
            }.setTextGetter {
                tr("bounty.max_number_of_rewards.value", it)
            }.build()
        )

        bountyCat.addEntry(
            creator.startBooleanToggle(
                tr("bounty.reverse_entry_matching_algorithm"),
                bounty.reverseMatchingAlgorithm
            ).setDefaultValue(false).setTooltip(
                tr("bounty.reverse_entry_matching_algorithm.tooltip")
            ).setSaveConsumer {
                bounty.reverseMatchingAlgorithm = it
            }.build()
        )

        bountyCat.addEntry(
            creator.startIntSlider(
                tr("bounty.bonus_time"),
                bounty.flatBonusTimePerBountyInSecs,
                0, 3600
            ).setDefaultValue(0).setTooltip(
                tr("bounty.bonus_time.tooltip")
            ).setSaveConsumer {
                bounty.flatBonusTimePerBountyInSecs = it
            }.build()
        )

        val clientCat = builder.getOrCreateCategory(tr("category.client"))

        clientCat.addEntry(
            creator.startBooleanToggle(
                tr("client.completion_toast_messages"),
                client.showCompletionToast
            ).setDefaultValue(true).setTooltip(
                tr("client.completion_toast_messages.tooltip")
            ).setSaveConsumer {
                client.showCompletionToast = it
            }.build()
        )

        val debugCat = builder.getOrCreateCategory(tr("category.debug"))

        debugCat.addEntry(
            creator.startBooleanToggle(
                tr("debug.enable_chaos_mode"),
                dbg.enabled
            ).setDefaultValue(false).setTooltip(
                tr("debug.enable_chaos_mode.tooltip")
            ).setSaveConsumer {
                dbg.enabled = it
                chaosMode = if (it) {
                    chaosMode ?: ChaosMode()
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

    private fun onSave() {
        BountifulIO.reloadConfig()
    }

}
