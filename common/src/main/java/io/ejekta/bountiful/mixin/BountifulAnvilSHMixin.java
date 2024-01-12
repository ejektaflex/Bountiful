package io.ejekta.bountiful.mixin;

import io.ejekta.bountiful.content.MixinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public class BountifulAnvilSHMixin {
    @Inject(method = "updateResult()V", at = @At("RETURN"))
    private void bo_makeCustomAnvilOutput(CallbackInfo ci) {
        MixinHelper.INSTANCE.modifyAnvilResults((AnvilScreenHandler) (Object) this);
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    private void bo_takeCustomAnvilOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        MixinHelper.INSTANCE.takeAnvilResults(player, stack, (AnvilScreenHandler) (Object) this);
    }

}
