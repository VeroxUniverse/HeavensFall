package net.pixeldream.heavensfall.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.HFBlocks;
import net.pixeldream.heavensfall.recipes.HFRecipes;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualRecipe;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualRecipe;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static IJeiRuntime jeiRuntime;

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIPlugin.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new DemonRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AngelRitualRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();

        List<DemonRitualRecipe> demonRituals = recipeManager.getAllRecipesFor(HFRecipes.DEMON_RITUAL_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(DemonRitualRecipeCategory.DEMON_RITUAL_RECIPE_TYPE, demonRituals);

        List<AngelRitualRecipe> angelRituals = recipeManager.getAllRecipesFor(HFRecipes.ANGEL_RITUAL_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .toList();
        registration.addRecipes(AngelRitualRecipeCategory.ANGEL_RITUAL_RECIPE_TYPE, angelRituals);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(HFBlocks.ALTAR_BLOCK.get()), DemonRitualRecipeCategory.DEMON_RITUAL_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(HFBlocks.ALTAR_PILLAR_BLOCK.get()), AngelRitualRecipeCategory.ANGEL_RITUAL_RECIPE_TYPE);
    }
}