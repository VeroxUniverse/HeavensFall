package net.pixeldream.heavensfall.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.HFBlocks;
import net.pixeldream.heavensfall.recipes.ritual.RitualHelper;
import net.pixeldream.heavensfall.recipes.ritual.RitualRecipe;
import oshi.util.tuples.Pair;

import java.util.List;

public class RitualRecipeCategory implements IRecipeCategory<RitualRecipe> {
    public static final ResourceLocation UUID = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "ritual_demonic");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "textures/gui/ritual_recipe_gui.png");

    public static final RecipeType<RitualRecipe> RITUAL_RECIPE_TYPE =
            new RecipeType<>(UUID, RitualRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    private static final List<Pair<Integer, Integer>> INPUT_COORDINATES = List.of(
            new Pair<>(80, 11),
            new Pair<>(50, 41),
            new Pair<>(110, 41),
            new Pair<>(80, 71)
    );

    private static final Pair<Integer, Integer> CENTER_COORDINATES = new Pair<>(80, 41);

    private static final Pair<Integer, Integer> OUTPUT_COORDINATES = new Pair<>(80, 116);

    public RitualRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 146);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(HFBlocks.PEDESTAL_BLOCK.get()));
    }

    @Override
    public RecipeType<RitualRecipe> getRecipeType() {
        return RITUAL_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("ui." + HeavensFallMod.MODID + ".ritual_recipe_jei");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RitualRecipe recipe, IFocusGroup focusGroup) {
        List<Ingredient> ingredients = recipe.getIngredients();

        int ingredientsToShow = Math.min(ingredients.size(), INPUT_COORDINATES.size());

        for (int i = 0; i < ingredientsToShow; i++) {
            Pair<Integer, Integer> coordinates = INPUT_COORDINATES.get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, coordinates.getA(), coordinates.getB())
                    .addIngredients(ingredients.get(i));
        }

        builder.addSlot(RecipeIngredientRole.INPUT, CENTER_COORDINATES.getA(), CENTER_COORDINATES.getB())
                .addIngredients(Ingredient.of(new ItemStack(recipe.getCentralItem())));

        Item resultItem = RitualHelper.getRitualRecipes().get(recipe);
        if (resultItem != null) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_COORDINATES.getA(), OUTPUT_COORDINATES.getB())
                    .addItemStack(new ItemStack(resultItem));
        } else {
            HeavensFallMod.LOGGER.warn("No result found for ritual recipe: {}", recipe);
        }
    }
}