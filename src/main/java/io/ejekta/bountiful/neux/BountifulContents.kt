package io.ejekta.bountiful.neux

import io.ejekta.bountiful.common.Bountiful
import io.ejekta.bountiful.common.content.BountyItem
import io.ejekta.bountiful.common.content.DecreeItem
import io.ejekta.bountiful.common.content.board.BoardBlock
import io.ejekta.kambrik.internal.KambricAutoRegistrar
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

object BountifulContents : KambricAutoRegistrar {

    override val modId = Bountiful.ID

    val BOUNTY_ITEM = "bounty" forItem BountyItem()

    val DECREE_ITEM = "decree" forItem DecreeItem()

    val BOARD = "board" forBlock BoardBlock()

    val BOARD_ITEM = "bountyboard" forItem BlockItem(BOARD, Item.Settings().group(ItemGroup.MISC))

}

