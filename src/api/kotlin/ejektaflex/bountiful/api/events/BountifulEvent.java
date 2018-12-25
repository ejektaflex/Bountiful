package ejektaflex.bountiful.api.events;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class BountifulEvent extends Event {


    @Cancelable
    public static class PopulateBountyBoardEvent extends BountifulEvent {

        //@Nonnull
        //public boardTileEntity

        public PopulateBountyBoardEvent(TileEntity boardTileEntity, ItemStack bounty) {

        }

        public static boolean fireEvent() {
            return true;
        }

    }


}
