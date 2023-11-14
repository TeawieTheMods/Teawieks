package vg.skye.teawieks.mixin.wheex;

import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import com.samsthenerd.wnboi.interfaces.KeyboundItem;
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import vg.skye.teawieks.TeawieksKeybinds;
import vg.skye.teawieks.wheex.ISpellbookScreen;
import vg.skye.teawieks.wheex.IotaWheelScreen;

@Mixin(ItemSpellbook.class)
public class ItemSpellbookMixin implements KeyboundItem, ISpellbookScreen {
    @Override
    public KeyMapping getKeyBinding(){
        return TeawieksKeybinds.INSTANCE.getIOTA_WHEEL_KEYBIND();
    }
    
    @Unique
    public IotaWheelScreen teawieks$screen = null;

    @Override
    public void teawieks$clearScreen() {
        teawieks$screen = null;
    }

    @Unique
    ItemStack teawieks$spellbook = null;

    @Override
    public AbstractContextWheelScreen getScreen(){
        Tuple<ItemStack, Boolean> spellbookResult = teawieks$getSpellbook();
        teawieks$spellbook = spellbookResult.getA();
        Screen oldScreen = Minecraft.getInstance().screen;
        if (teawieks$screen == null){
            teawieks$screen = new IotaWheelScreen(teawieks$spellbook, oldScreen);
        } else {
            teawieks$screen.setIotaProvider(teawieks$spellbook);
            teawieks$screen.setOldScreen(oldScreen);
        }
        teawieks$screen.setMainHand(spellbookResult.getB());
        return teawieks$screen;
    }

    @Unique
    public Tuple<ItemStack, Boolean> teawieks$getSpellbook(){
        if (Minecraft.getInstance().player == null) {
            return null;
        }
        ItemStack mainStack = Minecraft.getInstance().player.getMainHandItem();
        if (mainStack.getItem() instanceof ItemSpellbook){
            return new Tuple<>(mainStack, true);
        }
        ItemStack offStack = Minecraft.getInstance().player.getOffhandItem();
        if (offStack.getItem() instanceof ItemSpellbook){
            return new Tuple<>(offStack, false);
        }
        return null;
    }
}
