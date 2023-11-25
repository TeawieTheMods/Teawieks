package vg.skye.teawieks.mixin.maybechatreports;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vg.skye.teawieks.Teawieks;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow @Final private static Component CHAT_VALIDATION_FAILED_ERROR;

    @Redirect(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnectOnValidFail(Connection instance, Component message) {
        if (message != CHAT_VALIDATION_FAILED_ERROR) {
            instance.disconnect(message);
        } else {
            Teawieks.LOGGER.warn("ignoring chat validation failure");
        }
    }
}
