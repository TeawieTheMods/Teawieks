package vg.skye.teawieks.mixin.wherewasi;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vg.skye.teawieks.wherewasi.FullLocationData;
import vg.skye.teawieks.wherewasi.ILastLocationHolder;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ILastLocationHolder {
    @Unique
    FullLocationData teawieks$last = null;

    @Nullable
    @Override
    @Unique
    public FullLocationData getLastLocation() {
        return teawieks$last;
    }

    @Override
    @Unique
    public void saveLastLocation() {
        Player self = (Player) (Object) this;
        teawieks$last = new FullLocationData(
                (ServerLevel) self.level(),
                self.position(),
                self.getRotationVector()
        );
    }
}
