package vg.skye.teawieks.wheex

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.contains
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.samsthenerd.wnboi.screen.SpokeRenderer
import com.samsthenerd.wnboi.utils.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor.ARGB32
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class IotaSpokeRenderer(
    orX: Double,
    orY: Double,
    rad: Double,
    numSecs: Int,
    secIndex: Int,
    private val iotaIndex: Int,
    private val iotaProvider: ItemStack
): SpokeRenderer(orX, orY, rad, numSecs, secIndex) {
    companion object {
        private val random: RandomSource = RandomSource.create()
    }

    override fun select() {
        super.select()
        innerOutlineWeight = 1.0
    }

    private fun getPageName(index: Int): Component? {
        val idx = index + 1
        val key = idx.toString()
        val tag = iotaProvider.getCompound(ItemSpellbook.TAG_PAGE_NAMES)
        return if (tag != null && tag.contains(key, Tag.TAG_STRING)) {
            Component.Serializer.fromJson(tag.getString(key))
        } else {
            null
        }
    }

    override fun getColorFill(vI: Int, numOuter: Int, numInner: Int, isInner: Boolean): Int {
        if (!isInner) {
            val iotaNbt = WheexHelper.getIotaNBT(iotaIndex, iotaProvider) ?: return ARGB32.color(150, 200, 200, 200)
            val color: Int = IotaType.getColor(iotaNbt)
            return ARGB32.color(150, ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color))
        } else if (ItemSpellbook.getPage(iotaProvider, -1) == iotaIndex + 1) {
            val iotaNbt = WheexHelper.getIotaNBT(iotaIndex, iotaProvider) ?: return ARGB32.color(150, 200, 200, 200)
            val color: Int = IotaType.getColor(iotaNbt)

            return ARGB32.color(200, ARGB32.red(color), ARGB32.green(color), ARGB32.blue(color))
        }
        return ARGB32.color(96, 200, 200, 200)
    }

    override fun getColorOutline(vI: Int): Int {
        val colorPos = Vec3(
            vI + random.nextFloat() * 0.5,
            iotaIndex + random.nextFloat() * 0.5, vI + random.nextFloat() * 0.5
        ).scale(0.8)
        return IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player).colorProvider.getColor(currentTime.toFloat(), colorPos)
    }

    override fun renderLabel(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val label = iotaProvider.getCompound("teawieks:labels")?.getCompound((iotaIndex + 1).toString())
        if (label?.let { HexPattern.isPattern(it) } == true) {
            renderPatternLabel(context, HexPattern.fromNBT(label))
        } else {
            labelText = getPageName(iotaIndex)
            renderTextLabel(context, mouseX, mouseY, delta)
        }
    }

    override fun renderTextLabel(context: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {
        if (labelText != null) {
            val labelDistToUse = if (labelDist == null) ((outerRadius-innerRadius) / 2 + innerRadius).toInt() else labelDist
            val width = Minecraft.getInstance().font.width(labelText)
            val height = Minecraft.getInstance().font.lineHeight

            val x = originX + offsetX + cos(midAngle) * labelDistToUse
            val y = originY + offsetY + sin(midAngle) * labelDistToUse
            RenderUtils.renderText(
                context,
                labelText,
                (x - (width / 2)).roundToInt(),
                (y - (height / 2)).roundToInt(),
                -1
            )
        }
    }

    private fun renderPatternLabel(context: GuiGraphics, pattern: HexPattern) {
        val labelDistToUse = if (labelDist == null) ((outerRadius-innerRadius) / 2 + innerRadius).toInt() else labelDist
        val width = 24
        val height = 24

        val x = originX + offsetX + cos(midAngle) * labelDistToUse
        val y = originY + offsetY + sin(midAngle) * labelDistToUse
        WheexHelper.drawPattern(context, pattern, width.toFloat(), height.toFloat(), x.roundToInt(), y.roundToInt(),
            -2963256,
            -936236237,
            -936236237,
            0f,
            0f,
            2.4f
        )
    }
}