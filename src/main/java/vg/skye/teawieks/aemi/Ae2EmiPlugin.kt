package vg.skye.teawieks.aemi

import appeng.core.definitions.AEParts
import appeng.menu.me.items.CraftingTermMenu
import appeng.menu.me.items.PatternEncodingTermMenu
import de.mari_023.ae2wtlib.wct.WCTMenu
import de.mari_023.ae2wtlib.wet.WETMenu
import de.mari_023.ae2wtlib.wut.WUTHandler
import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.stack.EmiStack

// Code based on https://github.com/blocovermelho/ae2-emi-crafting
// which is licensed under MIT

@EmiEntrypoint
class Ae2EmiPlugin: EmiPlugin {
    override fun register(registry: EmiRegistry) {
        registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(AEParts.CRAFTING_TERMINAL.stack()));
        registry.addRecipeHandler(CraftingTermMenu.TYPE, Ae2RecipeHandler())
        registry.addRecipeHandler(PatternEncodingTermMenu.TYPE, Ae2PatternHandler())
        registry.addWorkstation(
            VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(
                WUTHandler.wirelessTerminals["crafting"]!!.universalTerminal()
            )
        )
        registry.addRecipeHandler(WCTMenu.TYPE, Ae2RecipeHandler())
        registry.addRecipeHandler(WETMenu.TYPE, Ae2PatternHandler())
    }
}