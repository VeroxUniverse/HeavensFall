package net.pixeldream.heavensfall.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.recipes.ritual.RitualHelper;
import net.pixeldream.heavensfall.recipes.ritual.RitualRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new RitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<RitualRecipe> ritualList = new ArrayList<>(RitualHelper.getRitualRecipes().keySet());

        registration.addRecipes(RitualRecipeCategory.RITUAL_RECIPE_TYPE, ritualList);
    }
}