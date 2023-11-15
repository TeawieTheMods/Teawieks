package vg.skye.teawieks.vazkiisbane

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import net.minecraft.network.chat.Component
import vazkii.botania.api.block_entity.BindableSpecialFlowerBlockEntity
import vazkii.botania.api.mana.ManaCollector
import vazkii.botania.api.mana.ManaPool

class OpGetMaxMana: ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pos = args.getBlockPos(0, argc)
        env.assertPosInRange(pos)
        return when (val block = env.world.getBlockEntity(pos)) {
            is ManaPool -> block.maxMana.asActionResult
            is BindableSpecialFlowerBlockEntity<*> -> block.maxMana.asActionResult
            is ManaCollector -> block.maxMana.asActionResult
            else -> throw MishapBadBlock(pos, Component.translatable("teawieks.hexcasting.mishaps.no_mana"))
        }
    }
}