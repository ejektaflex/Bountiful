package io.ejekta.bountiful.common.content

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.config.Decree
import io.ejekta.bountiful.common.config.Pool
import io.ejekta.bountiful.common.content.board.BoardBlock
import io.ejekta.bountiful.common.content.board.BoardBlockEntity
import io.ejekta.bountiful.common.content.gui.BoardScreenHandler
import io.ejekta.kambrik.registration.KambricAutoRegistrar
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

object BountifulContent : KambricAutoRegistrar {

    override fun manualRegister() {
        CommandRegistrationCallback.EVENT.register(BountifulCommands.commands)
    }

    val Decrees = mutableListOf<Decree>()

    val Pools = mutableListOf<Pool>()

    fun getDecrees(ids: Set<String>): Set<Decree> {
        return ids.mapNotNull { id ->
            Decrees.find { it.id == id }
        }.toSet()
    }

    val BOUNTY_ITEM = "bounty" forItem BountyItem()

    val DECREE_ITEM = "decree" forItem DecreeItem()

    val BOARD = "bountyboard" forBlock BoardBlock()

    val BOARD_ITEM = "bountyboard" forItem BlockItem(BOARD, Item.Settings().group(ItemGroup.MISC))

    val BOARD_ENTITY = "board-be".forBlockEntity(BOARD, ::BoardBlockEntity)

    val BOARD_SCREEN_HANDLER = forExtendedScreen(Bountiful.id("board"), ::BoardScreenHandler)

}