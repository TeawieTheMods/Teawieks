package vg.skye.teawieks.mixin.dyhex;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStaff.class)
public class ItemStaffMixin implements DyeableLeatherItem {
    public int getColor(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTagElement("display");
        if (nbtCompound != null && nbtCompound.contains("color", Tag.TAG_ANY_NUMERIC)) {
            return nbtCompound.getInt("color");
        }
        return 0xff7e4f;
    }
}
