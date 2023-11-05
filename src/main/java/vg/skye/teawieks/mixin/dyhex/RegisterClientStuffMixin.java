package vg.skye.teawieks.mixin.dyhex;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.client.RegisterClientStuff;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.ToIntFunction;

@Mixin(RegisterClientStuff.class)
public class RegisterClientStuffMixin {
    @Redirect(method = "registerColorProviders", at = @At(value = "INVOKE", target = "Lat/petrak/hexcasting/client/RegisterClientStuff;makeIotaStorageColorizer(Ljava/util/function/ToIntFunction;)Lnet/minecraft/client/color/item/ItemColor;", ordinal = 1, remap = false), remap = false)
    private static ItemColor dyeableSpellbook(ToIntFunction<ItemStack> getColor) {
        return (stack, idx) -> switch (idx) {
            case 1 -> ((IotaHolderItem) stack.getItem()).getColor(stack);
            case 2 -> ((DyeableLeatherItem) stack.getItem()).getColor(stack);
            default -> 0xffffffff;
        };
    }
}
