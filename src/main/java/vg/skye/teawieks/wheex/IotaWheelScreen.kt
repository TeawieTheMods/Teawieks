package vg.skye.teawieks.wheex

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.utils.contains
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S
import at.petrak.hexcasting.xplat.IClientXplatAbstractions
import com.samsthenerd.wnboi.screen.AbstractContextWheelScreen
import com.samsthenerd.wnboi.screen.SpokeRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW
import vg.skye.teawieks.Teawieks
import vg.skye.teawieks.TeawieksKeybinds
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt


class IotaWheelScreen(var iotaProvider: ItemStack, var oldScreen: Screen?): AbstractContextWheelScreen(Component.literal("Iota Selection Wheel"), 8) {
    private var onPage = (ItemSpellbook.getPage(iotaProvider, -1) - 1) / 8
    var mainHand = false

    init {
        requireKeydown = true
        keyBinding = TeawieksKeybinds.IOTA_WHEEL_KEYBIND
        numSections = 8
        tooltipTickDelay = 5
    }


    override fun triggerSpoke(index: Int) {
        selectedSection = -1
        val current = ItemSpellbook.getPage(iotaProvider, -1) - 1
        val target = index + onPage * 8
        val dist = abs(target - current)
        val invert = if (target >= current) -1.0 else 1.0
        Teawieks.LOGGER.info("{} {} {} {}", current, target, dist, invert)
        for (n in 1..dist) {
            IClientXplatAbstractions.INSTANCE.sendPacketToServer(
                MsgShiftScrollC2S(
                    if (mainHand) invert else 0.0,
                    if (!mainHand) invert else 0.0, false,
                    false, false
                )
            )
        }
    }

    override fun initConsts() {
        centerX = width / 2.0
        centerY = height / 2.0
        outerRadius = height * 0.3
        upperBoundRadius = outerRadius * 1.1
        lowerBoundRadius = outerRadius * 0.35
        angleOffset = Math.PI * 0.5 + Math.PI / numSections // so that the first spoke is centered at the top
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        // HexGloop.logPrint("currentSlot: " + (iotaProvider.currentSlot()-1) + ", perPage: " + iotaProvider.perPage() + ", onPage: " + onPage);
        super.render(context, mouseX, mouseY, delta)
    }

    override fun genSpokeRenderer(orX: Double, orY: Double, rad: Double, numSecs: Int, secIndex: Int): SpokeRenderer? {
        return IotaSpokeRenderer(
            orX, orY, rad, numSecs, secIndex,
            secIndex + onPage * 8, iotaProvider
        ).setGap(gap).setInnerRadius(outerRadius * 0.35).setAngleOffset(angleOffset)
    }

    override fun addAllSections() {
        initConsts()
        spokeRenderers = ArrayList() // just to clear it
        for (i in 0 until numSections) {
            spokeRenderers.add(genSpokeRenderer(centerX, centerY, outerRadius, numSections, i))
        }
        // additional spokes to select between pages of pages
        for (i in 0 until ItemSpellbook.MAX_PAGES / 8) {
            spokeRenderers.add(
                CenterModSpokeRenderer(
                    centerX, centerY, outerRadius * 0.34,
                    numSections, i
                ).setGap(gap).setInnerRadius(0.0).setAngleOffset(angleOffset)
            )
        }
        (spokeRenderers[onPage + numSections] as CenterModSpokeRenderer).currentPage = true
    }

    private fun close() {
        Minecraft.getInstance().screen = oldScreen
        (HexItems.SPELLBOOK as ISpellbookScreen).`teawieks$clearScreen`()
    }

    private fun getName(index: Int): Component? {
        val idx = index + 1
        val key = idx.toString()
        val tag = iotaProvider.getCompound(ItemSpellbook.TAG_PAGE_NAMES)
        return if (tag != null && tag.contains(key, Tag.TAG_COMPOUND)) {
            Component.Serializer.fromJson(tag.getString(key))
        } else {
            null
        }
    }

    override fun doRenderTooltip(context: GuiGraphics, mouseX: Int, mouseY: Int) {
        val iotaNBT = WheexHelper.getIotaNBT(selectedSection + onPage * 8, iotaProvider)
        if (iotaNBT == null) {
            context.renderTooltip(Minecraft.getInstance().font, Component.literal("Empty"), mouseX, mouseY)
        } else {
            val tooltip: MutableList<Component> = mutableListOf()
            tooltip.add(IotaType.getDisplay(iotaNBT))
            val name = getName(selectedSection + onPage * 8)
            if (name != null) {
                tooltip.add(name)
            }
            context.renderTooltip(Minecraft.getInstance().font, tooltip, Optional.empty(), mouseX, mouseY)
        }
    }

    private fun changePage(newPage: Int) {
        onPage = newPage
        addAllSections()
    }

    private fun didClickInner(mouseX: Int, mouseY: Int): Int {
        val diffX = mouseX - centerX
        val diffY = mouseY - centerY
        if (diffX == 0.0 && diffY == 0.0) {
            // center
            return -1
        }
        val dist = sqrt(diffX * diffX + diffY * diffY)
        if (dist >= lowerBoundRadius) {
            // outside the wheel
            return -1
        }
        // otherwise inside the wheel
        var theta: Double
        if (diffX == 0.0) {
            theta = if (diffY > 0) {
                Math.PI * 0.5
            } else {
                Math.PI * 1.5
            }
        } else {
            theta = atan(diffY / diffX)
            if (diffX < 0) {
                theta += Math.PI
            }
        }
        return getSectionIndexFromAngle(theta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            val inner = didClickInner(mouseX.toInt(), mouseY.toInt())
            if (inner >= 0) {
                changePage(inner)
                return true
            }
            return closeWheel(true)
        }
        if (button == 1) {
            val sectionIndex = getSectionIndexFromMouse(mouseX.toInt(), mouseY.toInt())
            if (sectionIndex in 0..7) {
                changePage(sectionIndex)
                return true
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
            var index = keyCode - GLFW.GLFW_KEY_0 - 1 // -1 so that 1 key triggers 0th index
            if (index == -1) index = 9
            if (modifiers == GLFW.GLFW_MOD_SHIFT && index <= ItemSpellbook.MAX_PAGES / 8) {
                changePage(index)
                return true
            }
            if (index < numSections) {
                triggerSpoke(index)
                close()
                return true
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }
}