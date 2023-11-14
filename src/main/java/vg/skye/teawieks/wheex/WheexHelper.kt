package vg.skye.teawieks.wheex

import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.utils.contains
import at.petrak.hexcasting.api.utils.getCompound
import at.petrak.hexcasting.client.render.*
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec2
import kotlin.math.max
import kotlin.math.min


object WheexHelper {
    fun getIotaNBT(index: Int, spellbook: ItemStack): CompoundTag? {
        val idx = index + 1
        val key = idx.toString()
        val tag = spellbook.getCompound(ItemSpellbook.TAG_PAGES)
        return if (tag != null && tag.contains(key, Tag.TAG_COMPOUND)) {
            tag.getCompound(key)
        } else {
            null
        }
    }

    fun drawPattern(
        context: GuiGraphics,
        pattern: HexPattern,
        width: Float,
        height: Float,
        x: Int,
        y: Int,
        outerColor: Int,
        innerColorMain: Int,
        innerColorAccent: Int,
        speed: Float,
        variance: Float,
        lineWidth: Float
    ) {
        val pair = getCenteredPattern(pattern, width, height, maxOf(width, height, 16.0f))
        val dots = pair.second
        val zappyPointsCentered = makeZappy(
            dots, findDupIndices(pattern.positions()),
            10, variance, speed, 0f, DEFAULT_READABILITY_OFFSET, DEFAULT_LAST_SEGMENT_LEN_PROP,
            0.0
        )
        val zappyPoints = mutableListOf<Vec2>()
        val pathfinderDots = mutableListOf<Vec2>()
        var minY = 1000000f
        var maxY = -1000000f
        var minX = 1000000f
        var maxX = -1000000f

        for (p in dots) {
            minY = min(minY, p.y)
            maxY = max(maxY, p.y)
            minX = min(minX, p.x)
            maxX = max(maxX, p.x)
        }
        val innerWidth = 0.4f

        val patWidth = (maxX - minX).toInt()
        val patHeight = (maxY - minY).toInt()

        val scale = min((width - lineWidth / 2) / patWidth, (height - lineWidth / 2) / patHeight)
        for (p in zappyPointsCentered) {
            zappyPoints.add(Vec2(scale * p.x + x, scale * p.y + y))
        }
        for (p in dots) {
            pathfinderDots.add(Vec2(scale * p.x + x, scale * p.y + y))
        }

        RenderSystem.enableBlend()
        RenderSystem.enableDepthTest()
        RenderSystem.setShader { GameRenderer.getPositionColorShader() }
        RenderSystem.disableCull()
        RenderSystem.blendFunc(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        )

        val mat = context.pose().last().pose()
        mat.translate(0f, 0f, 0.011f)

        drawLineSeq(
            mat, zappyPoints, lineWidth, 0f,
            outerColor, outerColor
        )
        drawLineSeq(
            mat,
            zappyPoints,
            lineWidth * innerWidth,
            0.01f,
            innerColorMain,
            innerColorAccent
        )
    }
}