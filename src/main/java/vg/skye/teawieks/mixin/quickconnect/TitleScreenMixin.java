package vg.skye.teawieks.mixin.quickconnect;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    private TitleScreenMixin(Component title) {
        super(title);
        throw new RuntimeException();
    }

    @Inject(method = "createNormalMenuOptions", at = @At(value = "TAIL"))
    private void addConnectButton(int y, int spacingY, CallbackInfo ci) {
        ServerData info = new ServerData(
                I18n.get("selectServer.defaultName"),
                "mc.teawi.es",
                false
        );
        info.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);

        var button = new Button.Builder(
                    Component.translatable("teawieks.connect_button"),
                    (buttonWidget) -> {
                        ConnectScreen.startConnecting(
                                this,
                                this.minecraft,
                                ServerAddress.parseString("mc.teawi.es"),
                                info,
                                true
                        );
                        buttonWidget.playDownSound(this.minecraft.getSoundManager());
                    }
                )
                .bounds(this.width / 2 + 105, y, 50, 20)
                .build();
        this.addRenderableWidget(button);
    }
}
