package vg.skye.teawieks.mixin.maybechatreports;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vg.skye.teawieks.Teawieks;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Redirect(method = "unpackAndApplyLastSeen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring chat validation failure");
    }

    @Redirect(method = "handleChatAck", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect2(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring chat validation failure");
    }

    @Redirect(method = "handleChatSessionUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect3(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring public key validation failure");
    }

    @Redirect(method = "handleMessageDecodeFailure", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect4(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring chat validation failure");
    }

    @Redirect(method = "tryHandleChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect5(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring chat validation failure");
    }

    @Redirect(method = "addPendingMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;disconnect(Lnet/minecraft/network/chat/Component;)V"))
    private void noDisconnect6(ServerGamePacketListenerImpl instance, Component textComponent) {
        Teawieks.LOGGER.warn("ignoring chat validation failure");
    }
}
