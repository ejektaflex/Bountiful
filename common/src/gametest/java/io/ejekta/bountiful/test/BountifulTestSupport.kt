package io.ejekta.bountiful.test

import io.ejekta.bountiful.bounty.BountyRarity
import io.ejekta.bountiful.bounty.types.BountyTypeRegistry
import io.ejekta.bountiful.components.BountyDataEntry
import io.ejekta.bountiful.components.BountyInfo
import io.ejekta.bountiful.components.BountyStack
import io.ejekta.bountiful.config.BountifulConfigData
import io.ejekta.bountiful.config.BountifulIO
import io.ejekta.bountiful.content.BountifulContent
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.board.BoardInventory
import io.ejekta.bountiful.content.board.BountyInventory
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.data.PoolEntry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object BountifulTestSupport {

    fun bountyStacks(board: BoardBlockEntity): List<ItemStack> {
        val inventory = board.fullInventoryCopy()
        return BoardInventory.BOUNTY_RANGE.map { inventory.getItem(it) }
    }

    fun decreeStacks(board: BoardBlockEntity): List<ItemStack> {
        val inventory = board.fullInventoryCopy()
        return BoardInventory.DECREE_RANGE.map { inventory.getItem(it) }
    }

    fun maskedInventory(board: BoardBlockEntity, player: Player): BoardInventory {
        val fullCopy = board.fullInventoryCopy()
        val maskedBounties = (fullCopy.bountySrc as BountyInventory).cloned(board.maskFor(player))
        return BoardInventory(board.blockPos, maskedBounties, fullCopy.decreeSrc)
    }

    fun saveBoard(board: BoardBlockEntity) = board.saveCustomOnly(board.level!!.registryAccess())

    fun loadBoard(board: BoardBlockEntity, tag: net.minecraft.nbt.CompoundTag) {
        board.loadCustomOnly(tag, board.level!!.registryAccess())
    }

    fun makeItemBounty(
        level: ServerLevel,
        objectiveItem: Item,
        objectiveAmount: Int,
        rewardItem: Item = Items.EMERALD,
        rewardAmount: Int = 1
    ): BountyStack {
        val stack = ItemStack(BountifulContent.BOUNTY_ITEM)
        return BountyStack(stack).apply {
            objs = listOf(
                BountyDataEntry(
                    id = "test.item_objective",
                    content = objectiveItem.builtInRegistryHolder().key().location().toString(),
                    rarity = BountyRarity.COMMON,
                    logicName = BountyTypeRegistry.ITEM.id.toString(),
                    amount = objectiveAmount
                )
            )
            rews = listOf(
                BountyDataEntry(
                    id = "test.item_reward",
                    content = rewardItem.builtInRegistryHolder().key().location().toString(),
                    rarity = BountyRarity.COMMON,
                    logicName = BountyTypeRegistry.ITEM.id.toString(),
                    amount = rewardAmount
                )
            )
            info = BountyInfo(
                rarity = BountyRarity.COMMON,
                timeStarted = level.gameTime - 40L,
                timeToComplete = 600L,
                timePickedUp = level.gameTime - 40L
            )
        }
    }

    fun makeEntityBounty(
        level: ServerLevel,
        target: EntityType<*>,
        amount: Int = 1,
        rewardItem: Item = Items.EMERALD
    ): BountyStack {
        val stack = ItemStack(BountifulContent.BOUNTY_ITEM)
        return BountyStack(stack).apply {
            objs = listOf(
                BountyDataEntry(
                    id = "test.entity_objective",
                    content = target.builtInRegistryHolder().key().location().toString(),
                    rarity = BountyRarity.COMMON,
                    logicName = BountyTypeRegistry.ENTITY.id.toString(),
                    amount = amount
                )
            )
            rews = listOf(
                BountyDataEntry(
                    id = "test.entity_reward",
                    content = rewardItem.builtInRegistryHolder().key().location().toString(),
                    rarity = BountyRarity.COMMON,
                    logicName = BountyTypeRegistry.ITEM.id.toString(),
                    amount = 1
                )
            )
            info = BountyInfo(
                rarity = BountyRarity.COMMON,
                timeStarted = level.gameTime - 20L,
                timeToComplete = 600L,
                timePickedUp = level.gameTime - 20L
            )
        }
    }

    fun createPool(id: String, currency: Boolean = false, entries: List<TestPoolEntry>): Pool {
        return Pool(id = id, currency = currency).apply {
            entries.mapIndexed { index, entry ->
                entry.toPoolEntry("$id.entry_$index")
            }.forEach(items::add)
        }
    }

    fun <T> withTemporaryContentAndConfig(
        config: BountifulConfigData = BountifulIO.configData,
        pools: List<Pool>,
        decrees: List<Decree>,
        action: () -> T
    ): T {
        val originalConfig = BountifulIO.configData
        val originalPools = BountifulContent.Pools
        val originalDecrees = BountifulContent.Decrees.toList()

        return try {
            BountifulIO.configData = config
            BountifulContent.populatePools(pools)
            BountifulContent.Decrees.clear()
            BountifulContent.Decrees.addAll(decrees)
            action()
        } finally {
            BountifulIO.configData = originalConfig
            BountifulContent.populatePools(originalPools)
            BountifulContent.Decrees.clear()
            BountifulContent.Decrees.addAll(originalDecrees)
        }
    }

    data class TestPoolEntry(
        val item: Item,
        val logicName: String = BountyTypeRegistry.ITEM.id.toString(),
        val amount: IntRange = 1..1,
        val unitWorth: Double,
        val rarity: BountyRarity = BountyRarity.COMMON
    ) {
        fun toPoolEntry(id: String): PoolEntry {
            return PoolEntry.create().apply {
                this.id = id
                type = net.minecraft.resources.ResourceLocation.parse(logicName)
                content = item.builtInRegistryHolder().key().location().toString()
                this.amount = PoolEntry.EntryRange(this@TestPoolEntry.amount.first, this@TestPoolEntry.amount.last)
                this.unitWorth = this@TestPoolEntry.unitWorth
                this.rarity = this@TestPoolEntry.rarity
            }
        }
    }
}
