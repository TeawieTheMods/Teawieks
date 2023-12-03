package vg.skye.teawieks.mixin.nickinator;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vg.skye.teawieks.nickinator.NickinatorData;

import java.util.UUID;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "getTabListDisplayName", at = @At("TAIL"), cancellable = true)
    void replacePlayerListName(CallbackInfoReturnable<Component> cir) {
        UUID uuid = ((Entity) (Object) this).getUUID();
        var nickname = NickinatorData.getInstance().get(uuid);
        if (nickname != null) {
            cir.setReturnValue(nickname);
        }
    }
}
