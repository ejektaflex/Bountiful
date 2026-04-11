package io.ejekta.bountiful.test

import io.ejekta.bountiful.bridge.Bountybridge
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulConfigData
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.BountyCreator
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.item.BountyItem
import io.ejekta.bountiful.content.item.DecreeItem
import io.ejekta.bountiful.data.Decree
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items

object BountifulGameTestCases {

    private val boardPos = BlockPos(1, 1, 1)
    private val villagerPos = BlockPos(3, 1, 1)

    fun generatedBountyHasObjectivesAndRewards(helper: GameTestHelper) {
        val level = helper.level
        val decree = BountifulContent.Decrees.firstOrNull { it.canSpawn }
            ?: error("No spawnable decrees were loaded for the test server")

        val bounty = BountyCreator.createBountyItem(level, helper.absolutePos(BlockPos.ZERO), setOf(decree), 0)
        val bountyStack = BountyStack(bounty)

        helper.assertTrue(bounty.`is`(BountifulContent.BOUNTY_ITEM), "Expected BountyCreator to return the registered bounty item")
        helper.assertFalse(bountyStack.objs.isEmpty(), "Generated bounty should contain at least one objective")
        helper.assertFalse(bountyStack.rews.isEmpty(), "Generated bounty should contain at least one reward")
        helper.assertTrue(bountyStack.info.timeStarted >= 0, "Generated bounty should have timing metadata")

        helper.succeed()
    }

    fun boardBootstrapPopulatesDecreesAndBounties(helper: GameTestHelper) {
        val originalFrequency = BountifulIO.configData.board.updateFrequencySecs
        BountifulIO.configData.board.updateFrequencySecs = 1

        helper.setBlock(boardPos, BountifulContent.BOARD.value)
        helper.succeedWhen {
            val board = helper.getBlockEntity<BoardBlockEntity>(boardPos)
            helper.assertTrue(
                BountifulTestSupport.decreeStacks(board).any { it.item is DecreeItem },
                "Fresh boards should reveal at least one decree"
            )
            helper.assertTrue(
                BountifulTestSupport.bountyStacks(board).any { it.item is BountyItem },
                "Fresh boards should generate at least one bounty"
            )
            BountifulIO.configData.board.updateFrequencySecs = originalFrequency
        }
    }

    fun completionFlowSchedulesVillager(helper: GameTestHelper) {
        helper.setBlock(boardPos, BountifulContent.BOARD.value)
        val board = helper.getBlockEntity<BoardBlockEntity>(boardPos)
        val villager = helper.spawnWithNoFreeWill(EntityType.VILLAGER, villagerPos)
        val player = helper.makeMockServerPlayerInLevel()
        val completedBounty = BountifulTestSupport.makeItemBounty(helper.level, Items.WHEAT, 4)

        board.updateUponBountyCompletion(player, completedBounty)

        helper.assertValueEqual(board.numCompleted, 1, "completed bounty count")
        val memory = villager.brain.getMemory(BountifulContent.MEM_MODULE_NEAREST_BOARD)
        helper.assertTrue(memory.isPresent, "A nearby villager should be scheduled to check the board after completion")
        helper.assertValueEqual(
            memory.orElseThrow(),
            GlobalPos.of(helper.level.dimension(), helper.absolutePos(boardPos)),
            "scheduled board memory"
        )

        helper.succeed()
    }

    fun entityKillProgressAdvancesMatchingBounty(helper: GameTestHelper) {
        val player = helper.makeMockServerPlayerInLevel()
        val trackedBounty = BountifulTestSupport.makeEntityBounty(helper.level, EntityType.ZOMBIE, amount = 2)
        val zombie = helper.spawn(EntityType.ZOMBIE, BlockPos(2, 1, 2))

        player.inventory.add(trackedBounty.stack)
        Bountybridge.handleEntityKills(helper.level, player, zombie)

        val playerBounty = BountyStack(player.inventory.items.first { it.item is BountyItem })
        val objective = playerBounty.objs.single()

        helper.assertValueEqual(playerBounty.progressOf(objective), 1, "entity bounty progress")
        helper.assertFalse(playerBounty.ping, "Partially completed entity bounties should not be marked complete yet")

        helper.succeed()
    }

    fun currencyGreedyModeUsesLargestRepresentableCurrency(helper: GameTestHelper) {
        val config = BountifulConfigData().apply {
            bounty.initialCountPreference = io.ejekta.bountiful.data.PoolEntry.EntryRange(1, 1)
            bounty.fillerCountPreference = io.ejekta.bountiful.data.PoolEntry.EntryRange(1, 1)
            bounty.fillerCurrencyPool = "test_currency"
        }
        val rewardPool = BountifulTestSupport.createPool(
            id = "test_rewards",
            entries = listOf(BountifulTestSupport.TestPoolEntry(Items.DIAMOND, unitWorth = 30.0))
        )
        val currencyPool = BountifulTestSupport.createPool(
            id = "test_currency",
            currency = true,
            entries = listOf(
                BountifulTestSupport.TestPoolEntry(Items.EMERALD_BLOCK, amount = 1..64, unitWorth = 10.0),
                BountifulTestSupport.TestPoolEntry(Items.EMERALD, amount = 1..64, unitWorth = 1.0)
            )
        )
        val decree = Decree(
            id = "test_currency_decree",
            objectives = mutableSetOf("test_currency"),
            rewards = mutableSetOf("test_rewards")
        )

        BountifulTestSupport.withTemporaryContentAndConfig(config, listOf(currencyPool, rewardPool), listOf(decree)) {
            val generated = BountyCreator.createBountyItem(helper.level, helper.absolutePos(boardPos), setOf(decree), 0)
            val bounty = BountyStack(generated)
            val objective = bounty.objs.single()

            helper.assertValueEqual(objective.content, Items.EMERALD_BLOCK.builtInRegistryHolder().key().location().toString(), "greedy currency content")
            helper.assertValueEqual(objective.amount, 3, "greedy currency amount")
        }

        helper.succeed()
    }

    fun serializationRoundTripPreservesBoardState(helper: GameTestHelper) {
        val originalFrequency = BountifulIO.configData.board.updateFrequencySecs
        BountifulIO.configData.board.updateFrequencySecs = 1
        helper.setBlock(boardPos, BountifulContent.BOARD.value)

        helper.succeedWhen {
            val board = helper.getBlockEntity<BoardBlockEntity>(boardPos)
            helper.assertTrue(
                BountifulTestSupport.bountyStacks(board).any { it.item is BountyItem },
                "Board should have generated a bounty before serialization"
            )

            val player = helper.makeMockServerPlayerInLevel()
            board.maskFor(player).add(BoardInventory.BOUNTY_RANGE.first)

            val saved = BountifulTestSupport.saveBoard(board)
            val restored = BoardBlockEntity(helper.absolutePos(boardPos), helper.getBlockState(boardPos))
            restored.setLevel(helper.level)
            BountifulTestSupport.loadBoard(restored, saved)

            helper.assertTrue(
                BountifulTestSupport.decreeStacks(restored).any { it.item is DecreeItem },
                "Restored boards should keep their decrees"
            )
            helper.assertTrue(
                BountifulTestSupport.bountyStacks(restored).any { it.item is BountyItem },
                "Restored boards should keep their bounty slots"
            )
            helper.assertTrue(
                BountifulTestSupport.maskedInventory(restored, player).getItem(BoardInventory.BOUNTY_RANGE.first).isEmpty,
                "Serialized player data should preserve which player claimed the slot"
            )
            BountifulIO.configData.board.updateFrequencySecs = originalFrequency
        }
    }

    fun maskedPlayersDoNotSeeTakenBounties(helper: GameTestHelper) {
        val originalFrequency = BountifulIO.configData.board.updateFrequencySecs
        BountifulIO.configData.board.updateFrequencySecs = 1
        helper.setBlock(boardPos, BountifulContent.BOARD.value)

        helper.succeedWhen {
            val board = helper.getBlockEntity<BoardBlockEntity>(boardPos)
            helper.assertTrue(
                BountifulTestSupport.bountyStacks(board).any { it.item is BountyItem },
                "Board should have generated a bounty before masking assertions"
            )

            val claimant = helper.makeMockServerPlayerInLevel()
            val otherPlayer = helper.makeMockServerPlayerInLevel()
            val slot = BoardInventory.BOUNTY_RANGE.first { !board.fullInventoryCopy().getItem(it).isEmpty }

            board.maskFor(claimant).add(slot)

            val claimantView = BountifulTestSupport.maskedInventory(board, claimant)
            val otherView = BountifulTestSupport.maskedInventory(board, otherPlayer)

            helper.assertTrue(claimantView.getItem(slot).isEmpty, "Claiming players should not see a taken bounty in their masked inventory")
            helper.assertFalse(otherView.getItem(slot).isEmpty, "Other players should still see untaken bounty slots")
            BountifulIO.configData.board.updateFrequencySecs = originalFrequency
        }
    }
}
