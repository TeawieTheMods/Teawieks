package vg.skye.teawieks.aemi

import appeng.api.stacks.GenericStack
import appeng.integration.modules.jeirei.EncodingHelper
import appeng.menu.SlotSemantics
import appeng.menu.me.items.PatternEncodingTermMenu
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.recipe.handler.EmiCraftContext
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraftforge.registries.ForgeRegistries


class Ae2PatternHandler<T: PatternEncodingTermMenu>: StandardRecipeHandler<T> {
    private val creativeInputSource: List<Slot> = ForgeRegistries.ITEMS.map { s ->
        val stack = ItemStack(s)
        stack.count = 64
        Slot(SimpleContainer(stack), 0, 0, 0)
    }.toList()

    override fun getInputSources(handler: T): List<Slot> {
        return creativeInputSource
    }

    override fun getCraftingSlots(handler: T): List<Slot> {
        return handler.getSlots(SlotSemantics.CRAFTING_GRID)
    }

    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        // I mean, probably
        return true
    }

    override fun craft(recipe: EmiRecipe, context: EmiCraftContext<T>): Boolean {
        val menu = context.screenHandler
        val recipeId = recipe.id ?: return false
        val vanillaRecipe = menu.player.level().recipeManager.byKey(recipeId)

        if (vanillaRecipe.isEmpty) {
            return false
        }

        val items = recipe.inputs.stream().map { ingredient ->
                if (ingredient.isEmpty) {
                    listOf()
                } else {
                    ingredient.emiStacks.map {
                        GenericStack.fromItemStack(it.itemStack)
                    }
                }
            }.toList()
        EncodingHelper.encodeCraftingRecipe(menu, vanillaRecipe.get(), items) { true }
        return true
    }
}