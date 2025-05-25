package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class DemonRitualRecipe {
    private final Item centralItem;
    private final ItemMultiSet inputItems;

    public DemonRitualRecipe(Item centralItem, ItemMultiSet inputItems) {
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
        List<Item> sortedItems = new ArrayList<>((Collection) inputItems.getUniqueItems());
        sortedItems.sort(Comparator.comparing(Item::toString));
        for (Item item : sortedItems) {
            int count = inputItems.getCount(item);
            for (int i = 0; i < count; i++) {
                ingredients.add(Ingredient.of(new ItemStack(item)));
            }
        }
        return ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DemonRitualRecipe that = (DemonRitualRecipe) o;

        if (!Objects.equals(centralItem, that.centralItem)) {
            return false;
        }

        return Objects.equals(inputItems, that.inputItems);
    }

    @Override
    public int hashCode() {
        int result = centralItem != null ? centralItem.hashCode() : 0;
        result = 31 * result + (inputItems != null ? inputItems.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DemonDemonRitualRecipe{" +
                "centralItem=" + centralItem +
                ", inputItems=" + inputItems +
                '}';
    }
}