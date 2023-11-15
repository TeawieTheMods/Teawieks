package vg.skye.teawieks.vazkiisbane

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import net.minecraft.network.chat.Component
import vazkii.botania.api.mana.ManaReceiver

class OpGetMana: ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        env.assertPosInRange(pos)
        val block = env.world.getBlockEntity(pos)
        if (block is ManaReceiver) {
            return block.currentMana.asActionResult
        } else {
            throw MishapBadBlock(pos, Component.translatable("teawieks.hexcasting.mishaps.no_mana"))
        }
    }
}