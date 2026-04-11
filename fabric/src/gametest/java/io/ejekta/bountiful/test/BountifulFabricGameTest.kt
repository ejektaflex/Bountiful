package io.ejekta.bountiful.test

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import net.minecraft.gametest.framework.GameTest
import net.minecraft.gametest.framework.GameTestHelper

class BountifulFabricGameTest : FabricGameTest {

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun generatedBountyHasObjectivesAndRewards(helper: GameTestHelper) = BountifulGameTestCases.generatedBountyHasObjectivesAndRewards(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun boardBootstrapPopulatesDecreesAndBounties(helper: GameTestHelper) = BountifulGameTestCases.boardBootstrapPopulatesDecreesAndBounties(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun completionFlowSchedulesVillager(helper: GameTestHelper) = BountifulGameTestCases.completionFlowSchedulesVillager(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun entityKillProgressAdvancesMatchingBounty(helper: GameTestHelper) = BountifulGameTestCases.entityKillProgressAdvancesMatchingBounty(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun currencyGreedyModeUsesLargestRepresentableCurrency(helper: GameTestHelper) = BountifulGameTestCases.currencyGreedyModeUsesLargestRepresentableCurrency(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun serializationRoundTripPreservesBoardState(helper: GameTestHelper) = BountifulGameTestCases.serializationRoundTripPreservesBoardState(helper)

    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    fun maskedPlayersDoNotSeeTakenBounties(helper: GameTestHelper) = BountifulGameTestCases.maskedPlayersDoNotSeeTakenBounties(helper)
}
