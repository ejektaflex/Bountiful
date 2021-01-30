package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.content.board.BoardBlock
import io.ejekta.bountiful.common.content.board.BoardBlockEntity
import io.ejekta.bountiful.common.content.gui.BoardScreenHandler
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.registry.Registry

object BountifulContent {

    val BOUNTY_ITEM = BountyItem()

    val BOARD = BoardBlock()

    val BOARD_ENTITY: BlockEntityType<BoardBlockEntity> = BlockEntityType.Builder
        .create(::BoardBlockEntity, BOARD).build(null)

    val BOARD_SCREEN_HANDLER: ScreenHandlerType<BoardScreenHandler> = ScreenHandlerRegistry
        .registerSimple(Bountiful.id("board"), ::BoardScreenHandler)

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