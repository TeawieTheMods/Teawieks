package vg.skye.teawieks.mixin.procheat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {
    @Redirect(method = "handleBlockBreakAction", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;canReach(Lnet/minecraft/core/BlockPos;D)Z"
    ))
    private boolean getEyePos(ServerPlayer instance, BlockPos blockPos, double v) {
        return true;
    }
}
