package vg.skye.teawieks.scaler

import at.petrak.hexcasting.api.utils.getDouble
import at.petrak.hexcasting.api.utils.getInt
import at.petrak.hexcasting.api.utils.putInt
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import virtuoel.pehkui.api.ScaleTypes

object ScalerItem: Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val item = player.getItemInHand(usedHand)
        if (player.isShiftKeyDown) {
            item.putInt("scale", 100)
            player.displayClientMessage(Component.translatable("teawieks.scaler.scale", 1).withStyle(ChatFormatting.GREEN), true)
            return InteractionResultHolder.sidedSuccess(item, level.isClientSide)
        } else {
            val scale = item.getInt("scale", 100).toFloat() / 100
            ScaleTypes.WIDTH.getScaleData(player).targetScale = scale
            ScaleTypes.HEIGHT.getScaleData(player).targetScale = scale
            return InteractionResultHolder.pass(item)
        }
    }
}