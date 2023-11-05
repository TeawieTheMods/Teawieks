package vg.skye.teawieks.mixin.procheat;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    @Shadow public abstract void startFallFlying();

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void allowDeploys(CallbackInfoReturnable<Boolean> cir)
    {
        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer sp && sp.getServer().isDedicatedServer())
        {
            ItemStack item = getItemBySlot(EquipmentSlot.CHEST);
            if (item.canElytraFly(player)) {
                startFallFlying();
                cir.setReturnValue(true);
            }
        }
    }
}
