package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.pixeldream.heavensfall.recipes.HFRecipes;
import net.pixeldream.heavensfall.recipes.RitualRecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public class DemonRitualRecipe implements Recipe<SingleRecipeInput>, RitualRecipeSerializer.RitualRecipeAccessor {
    private final Item centralItem;
    private final ItemMultiSet inputItems;
    private final ItemStack result;

    public DemonRitualRecipe(Item centralItem, ItemMultiSet inputItems, ItemStack result) {
        this.centralItem = centralItem;
        this.inputItems = inputItems;
        this.result = result;
    }

    @Override
    public Item getCentralItem() { return centralItem; }

    @Override
    public List<Item> getInputList() {
        List<Item> list = new ArrayList<>();
        for (Item item : inputItems.getUniqueItems()) {
            for (int i = 0; i < inputItems.getCount(item); i++) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (Item item : inputItems.getUniqueItems()) {
            int count = inputItems.getCount(item);
            for (int i = 0; i < count; i++) {
                ingredients.add(Ingredient.of(new ItemStack(item)));
            }
        }

        return ingredients;
    }

    @Override
    public ItemStack getResultStack() { return result; }

    public boolean matches(Item central, ItemMultiSet pedestals) {
        return this.centralItem == central && this.inputItems.equals(pedestals);
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return input.item().is(this.centralItem);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) { return result; }

    @Override
    public RecipeSerializer<?> getSerializer() { return HFRecipes.DEMON_RITUAL_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return HFRecipes.DEMON_RITUAL_TYPE.get(); }

    public ItemMultiSet getInputItems() { return inputItems; }
}