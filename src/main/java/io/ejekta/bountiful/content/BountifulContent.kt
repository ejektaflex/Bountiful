package io.ejekta.bountiful.content

import io.ejekta.bountiful.Bountiful
import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

object BountifulContent : KambrikAutoRegistrar {

    override fun beforeRegistration() {
        CommandRegistrationCallback.EVENT.register(BountifulCommands)
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