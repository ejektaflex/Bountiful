package io.ejekta.bountiful.content

import io.ejekta.bountiful.data.Decree
import io.ejekta.bountiful.data.Pool
import io.ejekta.bountiful.content.board.BoardBlock
import io.ejekta.bountiful.content.board.BoardBlockEntity
import io.ejekta.bountiful.content.gui.BoardScreenHandler
import io.ejekta.kambrik.registration.KambrikAutoRegistrar
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups

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

    val BOARD_ITEM = "bountyboard" forItem BlockItem(BOARD, Item.Settings())

    val BOARD_ENTITY = "board-be".forBlockEntity(BOARD, ::BoardBlockEntity)

    val BOARD_SCREEN_HANDLER = "board".forExtendedScreen(::BoardScreenHandler)

    init {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register { e ->
            e.add(DECREE_ITEM)
            e.add(BOARD_ITEM)
        }
    }

}