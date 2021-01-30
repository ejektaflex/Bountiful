package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.Bountiful
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry

object BountifulContent {

    val BOUNTY_ITEM = BountyItem()

    val BOARD = BountyBoard()

    val BOARD_ENTITY: BlockEntityType<BoardBlockEntity> =
        BlockEntityType.Builder.create(::BoardBlockEntity, BOARD).build(null)

    fun register() {
        CommandRegistrationCallback.EVENT.register(BountifulCommands.registerCommands())
        Registry.register(Registry.ITEM, Bountiful.id("bounty"), BOUNTY_ITEM)
        Registry.register(Registry.BLOCK, Bountiful.id("bountyboard"), BOARD)
        Registry.register(Registry.ITEM, Bountiful.id("bountyboard"),
            BlockItem(BOARD, Item.Settings().group(ItemGroup.MISC))
        )
        Registry.register(Registry.BLOCK_ENTITY_TYPE, Bountiful.id("board-be"), BOARD_ENTITY)
    }

}