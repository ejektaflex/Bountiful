package ejektaflex.bountiful.block

import ejektaflex.bountiful.content.ModContent
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntity

class BountyTE : TileEntity(ModContent.Blocks.BOUNTYTILEENTITY), ITickableTileEntity {

    override fun tick() {
        if (!world!!.isRemote) {
            if (world!!.gameTime % 20 == 0L) {
                println("BountyTE::tick")
            }
        }
    }

}