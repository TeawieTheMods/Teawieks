package vg.skye.teawieks.mixin.nickinator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vg.skye.teawieks.nickinator.NickinatorData;

import java.util.UUID;

@Mixin(Player.class)
public class PlayerMixin {
    @ModifyArg(method = "getDisplayName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/scores/PlayerTeam;formatNameForTeam(Lnet/minecraft/world/scores/Team;Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"))
    private Component replaceNickname(Component text) {
        UUID uuid = ((Entity) (Object) this).getUUID();
        var name = NickinatorData.getInstance().get(uuid);
        if (name != null) {
            return name;
        }
        return text;
    }
}
