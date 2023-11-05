package vg.skye.teawieks.aemi

import appeng.api.stacks.AEItemKey
import appeng.api.stacks.AEKey
import appeng.core.sync.network.NetworkHandler
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket
import appeng.integration.modules.jeirei.EncodingHelper
import appeng.menu.SlotSemantics
import appeng.menu.me.common.GridInventoryEntry
import appeng.menu.me.items.CraftingTermMenu
import com.google.common.base.Preconditions
import dev.emi.emi.api.recipe.EmiCraftingRecipe
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.recipe.handler.EmiCraftContext
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.core.NonNullList
import net.minecraft.world.SimpleContainer
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapedRecipe


class Ae2RecipeHandler<T: CraftingTermMenu>: StandardRecipeHandler<T> {
    override fun supportsRecipe(recipe: EmiRecipe): Boolean {
        return recipe.category == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree()
    }

    override fun getCraftingSlots(handler: T): MutableList<Slot> {
        return handler.getSlots(SlotSemantics.CRAFTING_GRID)
    }

    override fun getInputSources(handler: T): MutableList<Slot> {
        val clientRepo = handler.clientRepo ?: return mutableListOf()

        val sources = clientRepo.allEntries
            .stream()
            .filter { s: GridInventoryEntry -> s.what != null }
            .filter { s: GridInventoryEntry -> s.storedAmount > 0 || !s.isCraftable }
            .map { s: GridInventoryEntry ->
                val stack = s.what!!.wrapForDisplayOrFilter()
                val amount = s.storedAmount
                val clamped = (if (amount > 64) 64 else amount).toInt()
                stack.count = clamped
                Slot(SimpleContainer(stack), 0, 0, 0)
            }.toList().toMutableList()

        sources.addAll(handler.getSlots(SlotSemantics.PLAYER_INVENTORY))
        sources.addAll(handler.getSlots(SlotSemantics.PLAYER_HOTBAR))
        sources.addAll(handler.getSlots(SlotSemantics.CRAFTING_GRID))

        return sources
    }

    override fun craft(recipe: EmiRecipe, context: EmiCraftContext<T>): Boolean {
        if (recipe is EmiCraftingRecipe) {
            val recipeId = recipe.getId()
            val candidates = findTemplateCandidates(recipe, context.screenHandler)

            // Sends a packet to the server, informing that a crafting was requested. Since that's how AE2 handles crafting requests.
            NetworkHandler.instance().sendToServer(FillCraftingGridFromRecipePacket(recipeId, candidates, false))
            return true
        }

        return false
    }

    private fun findTemplateCandidates(recipe: EmiRecipe, menu: T): NonNullList<ItemStack> {
        val prioritizedInventory: Map<AEKey, Int> = EncodingHelper.getIngredientPriorities(menu, Comparator.comparing(GridInventoryEntry::getStoredAmount))
        val list = NonNullList.withSize(9, ItemStack.EMPTY)
        val normalizedMatrix = ensureSize(recipe)
        for (i in 0 until normalizedMatrix.size) {
            val ingredient: EmiIngredient = normalizedMatrix[i]
            if (ingredient.isEmpty) continue

            // Get the best viable stack for this crafting, handling tags and taking items from the ME Inventory first.
            val stack = prioritizedInventory.entries.stream()
                .filter { (key): Map.Entry<AEKey, Int> ->
                    (key is AEItemKey
                            && EmiIngredient.areEqual(
                        ingredient,
                        EmiStack.of(key.toStack())
                    ))
                }
                .max(Comparator.comparingInt { (_, value) -> value })
                .map { (key): Map.Entry<AEKey, Int> -> (key as AEItemKey).toStack() }
                .orElse(ingredient.emiStacks[0].itemStack)
            list[i] = stack
        }
        return list
    }

    private fun ensureSize(recipe: EmiRecipe): NonNullList<EmiIngredient> {
        val list = NonNullList.withSize(9, EmiIngredient.of(Ingredient.EMPTY))
        val ingredients = recipe.inputs
        Preconditions.checkArgument(ingredients.size <= 9)
        if (recipe is ShapedRecipe) {
            val width: Int = recipe.width
            val height: Int = recipe.height
            Preconditions.checkArgument(width <= 3 && height <= 3)
            for (h in 0 until height) {
                for (w in 0 until width) {
                    val source = w + h * width
                    val target = w + h * 3
                    val ingredient = ingredients[source]
                    list[target] = ingredient
                }
            }
        } else {
            for (i in ingredients.indices) {
                list[i] = ingredients[i]
            }
        }
        return list
    }
}