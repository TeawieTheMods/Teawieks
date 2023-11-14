package vg.skye.teawieks

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object TeawieksKeybinds {
    val IOTA_WHEEL_KEYBIND = KeyMapping(
        "key.teawieks.open_iota_wheel",
        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.teawieks"
    )
}