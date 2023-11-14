package vg.skye.teawieks.wheex

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.getOrCreateCompound
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.items.storage.ItemSpellbook
import net.minecraft.world.item.ItemStack


class OpSetLabel: SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val stack = env.getHeldItemToOperateOn { s -> s.item is ItemSpellbook }?.stack
            ?: throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), null, "iota.write")

        val label =
            if (args[0] !is NullIota)
                args.getPattern(0, argc)
            else
                null
        return SpellAction.Result(
            Spell(label, stack),
            MediaConstants.DUST_UNIT,
            listOf()
        )
    }
    private data class Spell(val label: HexPattern?, val stack: ItemStack) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val index = ItemSpellbook.getPage(stack, 0)
            val labels = stack.getOrCreateCompound("teawieks:labels")
            if (label != null) {
                val serialized = label.serializeToNBT()
                labels.putCompound(index.toString(), serialized)
            } else {
                labels.remove(index.toString())
            }
            stack.putCompound("teawieks:labels", labels)
        }
    }
}