package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ItemMultiSet {
    private final Map<Item, Integer> itemCounts;

    public ItemMultiSet() {
        this.itemCounts = new HashMap<>();
    }

    public ItemMultiSet(Iterable<Item> items) {
        this.itemCounts = new HashMap<>();
        for (Item item : items) {
            add(item);
        }
    }

    public void add(Item item) {
        itemCounts.put(item, itemCounts.getOrDefault(item, 0) + 1);
    }

    public int getCount(Item item) {
        return itemCounts.getOrDefault(item, 0);
    }

    public boolean containsAll(ItemMultiSet other) {
        for (Map.Entry<Item, Integer> entry : other.itemCounts.entrySet()) {
            if (getCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Iterable<Item> getUniqueItems() {
        return itemCounts.keySet();
    }

    public int size() {
        return itemCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return itemCounts.isEmpty();
    }

    public Map<Item, Integer> getItemCounts() {
        return new HashMap<>(itemCounts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemMultiSet that = (ItemMultiSet) o;

        if (itemCounts.size() != that.itemCounts.size()) {
            return false;
        }

        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            Item item = entry.getKey();
            Integer count = entry.getValue();

            if (!count.equals(that.itemCounts.getOrDefault(item, 0))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            result = 31 * result + Objects.hashCode(entry.getKey()) * entry.getValue();
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ItemMultiSet{");
        for (Map.Entry<Item, Integer> entry : itemCounts.entrySet()) {
            sb.append(entry.getKey()).append("×").append(entry.getValue()).append(", ");
        }
        if (!itemCounts.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}