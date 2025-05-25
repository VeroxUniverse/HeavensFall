package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Objects;

public class AngelRitualRecipe {
    private final Item centralItem;
    private final ItemMultiSet inputItems;

    public AngelRitualRecipe(Item centralItem, ItemMultiSet inputItems) {
        this.centralItem = centralItem;
        this.inputItems = inputItems;
    }

    public Item getCentralItem() {
        return centralItem;
    }

    public ItemMultiSet getInputItems() {
        return inputItems;
    }

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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AngelRitualRecipe other)) return false;
        return Objects.equals(centralItem, other.centralItem) &&
                Objects.equals(inputItems, other.inputItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(centralItem, inputItems);
    }
}
