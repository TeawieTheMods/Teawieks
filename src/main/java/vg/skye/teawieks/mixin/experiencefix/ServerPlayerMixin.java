package vg.skye.teawieks.mixin.experiencefix;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Shadow public ServerGamePacketListenerImpl connection;

    @Inject(method = "triggerDimensionChangeTriggers", at = @At("TAIL"))
    void afterTriggerDimensionChangeTriggers(ServerLevel level, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        connection.send(new ClientboundSetExperiencePacket(
                player.experienceProgress,
                player.totalExperience,
                player.experienceLevel
        ));
        connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
        player.getActiveEffects().forEach(effect -> {
            connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), effect));
        });
    }
}
