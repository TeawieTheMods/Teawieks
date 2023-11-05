package vg.skye.teawieks.mixin.scaler;


import at.petrak.hexcasting.api.utils.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vg.skye.teawieks.scaler.ScalerItem;

@Mixin(MsgShiftScrollC2S.class)
public class MsgShiftScrollC2SMixin {
    @Shadow @Final private boolean invertAbacus;

    @Shadow @Final private boolean isCtrl;

    @Inject(method = "handleForHand", at = @At("TAIL"), remap = false)
    void handleForHand(ServerPlayer sender, InteractionHand hand, double delta, CallbackInfo ci) {
        if (delta != 0) {
            ItemStack stack = sender.getItemInHand(hand);
            if (stack.getItem() == ScalerItem.INSTANCE) {
                if (invertAbacus) {
                    delta = -delta;
                }

                var increase = delta < 0;
                int num = NBTHelper.getInt(stack, "scale", 100);

                int shiftDelta = this.isCtrl ? 1 : 10;

                int scale = Math.max((int) Math.floor(Math.abs(delta)), 1);

                num += scale * shiftDelta * (increase ? 1 : -1);
                num = Math.max(1, Math.min(100, num));
                NBTHelper.putInt(stack, "scale", num);

                sender.displayClientMessage(Component.translatable("teawieks.scaler.scale", num / 100.).withStyle(ChatFormatting.GREEN), true);
            }
        }
    }
}
