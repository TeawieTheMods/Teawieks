package vg.skye.teawieks.aemi

import appeng.api.stacks.GenericStack
import appeng.integration.modules.jeirei.EncodingHelper
import appeng.menu.me.items.PatternEncodingTermMenu
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.recipe.handler.EmiCraftContext
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler
import net.minecraft.world.inventory.Slot


class Ae2PatternHandler<T: PatternEncodingTermMenu>: StandardRecipeHandler<T> {
    override fun getInputSources(handler: T): List<Slot> {
        return listOf()
    }

    override fun canCraft(recipe: EmiRecipe?, context: EmiCraftContext<T>?): Boolean {
        return true
    }

    override fun getCraftingSlots(handler: T): List<Slot> {
        return listOf()
    }

    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        // I mean, probably
        return true
    }

    override fun craft(recipe: EmiRecipe, context: EmiCraftContext<T>): Boolean {
        val menu = context.screenHandler
        val items = recipe.inputs.map { ingredient ->
            if (ingredient.isEmpty) {
                listOf()
            } else {
                ingredient.emiStacks.map {
                    GenericStack.fromItemStack(it.itemStack)
                }
            }
        }
        when (recipe.category) {
            VanillaEmiRecipeCategories.CRAFTING,
            VanillaEmiRecipeCategories.STONECUTTING,
            VanillaEmiRecipeCategories.SMITHING -> {
                val recipeId = recipe.id ?: return false
                val vanillaRecipe = menu.player.level().recipeManager.byKey(recipeId)
                if (vanillaRecipe.isEmpty) {
                    return false
                }
                EncodingHelper.encodeCraftingRecipe(menu, vanillaRecipe.get(), items) { true }
            }
            else -> EncodingHelper.encodeProcessingRecipe(menu, items, recipe.outputs.map { GenericStack.fromItemStack(it.itemStack) })
        }
        return true
    }
}