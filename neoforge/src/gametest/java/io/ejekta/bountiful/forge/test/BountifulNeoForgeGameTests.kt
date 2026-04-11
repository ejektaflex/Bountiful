package io.ejekta.bountiful.forge.test

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.test.BountifulGameTestCases
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper
import net.neoforged.neoforge.gametest.GameTestHolder
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate

@GameTestHolder(Bountiful.ID)
@PrefixGameTestTemplate(false)
object BountifulNeoForgeGameTests {

    @JvmStatic
    @GameTest(template = "empty")
    fun generatedBountyHasObjectivesAndRewards(helper: GameTestHelper) = BountifulGameTestCases.generatedBountyHasObjectivesAndRewards(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun boardBootstrapPopulatesDecreesAndBounties(helper: GameTestHelper) = BountifulGameTestCases.boardBootstrapPopulatesDecreesAndBounties(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun completionFlowSchedulesVillager(helper: GameTestHelper) = BountifulGameTestCases.completionFlowSchedulesVillager(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun entityKillProgressAdvancesMatchingBounty(helper: GameTestHelper) = BountifulGameTestCases.entityKillProgressAdvancesMatchingBounty(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun currencyGreedyModeUsesLargestRepresentableCurrency(helper: GameTestHelper) = BountifulGameTestCases.currencyGreedyModeUsesLargestRepresentableCurrency(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun serializationRoundTripPreservesBoardState(helper: GameTestHelper) = BountifulGameTestCases.serializationRoundTripPreservesBoardState(helper)

    @JvmStatic
    @GameTest(template = "empty")
    fun maskedPlayersDoNotSeeTakenBounties(helper: GameTestHelper) = BountifulGameTestCases.maskedPlayersDoNotSeeTakenBounties(helper)
}
