package io.ejekta.bountiful.mixin;

import io.ejekta.bountiful.content.MixinHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.item.ItemStack;

@Mixin(AnvilMenu.class)
public class BountifulAnvilMenuMixin {
    @Inject(method = "createResult", at = @At("RETURN"))
    private void bo_makeCustomAnvilOutput(CallbackInfo ci) {
        MixinHelper.INSTANCE.modifyAnvilResults((AnvilMenu) (Object) this);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void bo_takeCustomAnvilOutput(Player pPlayer, ItemStack pStack, CallbackInfo ci) {
        MixinHelper.INSTANCE.takeAnvilResults(pPlayer, pStack, (AnvilMenu) (Object) this);
    }
}
