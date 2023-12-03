package vg.skye.teawieks.mixin.fixgel;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;

@Mixin(value = FlugelTiaraItem.class, remap = false)
public class FlugelTiaraItemMixin {
    @Redirect(method = "shouldPlayerHaveFlight", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;contains(Lnet/minecraft/world/item/ItemStack;)Z", remap = true), remap = false)
    private static boolean eyeFix(Inventory instance, ItemStack list) {
        for (ItemStack item: instance.items) {
            if (item.is(BotaniaItems.flugelEye)) {
                return true;
            }
        }
        return false;
    }
}
