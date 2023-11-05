package vg.skye.teawieks.mixin.scaler;

import at.petrak.hexcasting.client.ShiftScrollListener;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vg.skye.teawieks.scaler.ScalerItem;

@Mixin(ShiftScrollListener.class)
public class ShiftScrollListenerMixin {
    @Inject(method = "IsScrollableItem", remap = false, at = @At("HEAD"), cancellable = true)
    private static void scalerIsScrollable(Item item, CallbackInfoReturnable<Boolean> cir) {
        if (item == ScalerItem.INSTANCE) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
