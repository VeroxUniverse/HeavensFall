package net.pixeldream.heavensfall.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualHelper;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualRecipe;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualHelper;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualRecipe;

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
        registration.addRecipeCategories(new DemonRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AngelRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<DemonRitualRecipe> demonRitualList = new ArrayList<>(DemonRitualHelper.getDemonRitualRecipes().keySet());

        registration.addRecipes(DemonRitualRecipeCategory.DEMON_RITUAL_RECIPE_TYPE, demonRitualList);

        List<AngelRitualRecipe> angelRitualList = new ArrayList<>(AngelRitualHelper.getAngelRitualRecipes().keySet());

        registration.addRecipes(AngelRitualRecipeCategory.ANGEL_RITUAL_RECIPE_RECIPE_TYPE, angelRitualList);
    }
}