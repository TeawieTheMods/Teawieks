package vg.skye.teawieks.wheex

import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.samsthenerd.wnboi.screen.SpokeRenderer
import net.minecraft.client.Minecraft
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.Vec3

class CenterModSpokeRenderer(
    orX: Double,
    orY: Double,
    rad: Double,
    numSecs: Int,
    secIndex: Int
): SpokeRenderer(orX, orY, rad, numSecs, secIndex) {
    companion object {
        private val random: RandomSource = RandomSource.create()
    }

    init {
        innerOutlineWeight = 0.5
        numDivisions = 10
    }

    var currentPage = false
    override fun getColorFill(vI: Int, numOuter: Int, numInner: Int, isInner: Boolean): Int {
        if (currentPage) {
            val colorPos = if (isInner) {
                Vec3(
                    random.nextFloat() * 0.5,
                    sectionIndex + random.nextFloat() * 0.5, random.nextFloat() * 0.5
                ).scale(0.3)
            } else {
                Vec3(
                    sectionIndex + random.nextFloat() * 0.5,
                    random.nextFloat() * 0.5, sectionIndex + random.nextFloat() * 0.5
                ).scale(0.3)
            }
            return IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player).colorProvider.getColor(currentTime.toFloat(), colorPos)
        }
        return 0x00000000 // clear
    }

    override fun getColorOutline(vI: Int): Int {
        if (currentPage) {
            return 0x00000000 // clear
        }
        val colorPos = Vec3(
            vI + random.nextFloat() * 0.5,
            sectionIndex + random.nextFloat() * 0.5,
            sectionIndex + random.nextFloat() * 0.5
        ).scale(0.25)
        return IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player).colorProvider.getColor(currentTime.toFloat(), colorPos)
    }
}