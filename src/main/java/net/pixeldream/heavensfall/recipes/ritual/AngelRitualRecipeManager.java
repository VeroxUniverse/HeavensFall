package net.pixeldream.heavensfall.recipes.ritual;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.pixeldream.heavensfall.HeavensFallMod;

import java.util.Map;

public class AngelRitualRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public AngelRitualRecipeManager() {
        super(GSON, "angel_ritual_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        HeavensFallMod.LOGGER.info("Loading angel ritual recipes...");
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(entry.getValue(), "angel ritual recipe");
                loadRitualRecipe(entry.getKey(), json);
            } catch (Exception e) {
                HeavensFallMod.LOGGER.error("Failed to load angel ritual recipe {}: {}", entry.getKey(), e.getMessage());
            }
        }

        AngelRitualHelper.debugPrintAllRecipes();
    }

    private void loadRitualRecipe(ResourceLocation id, JsonObject json) {
        String centralStr = GsonHelper.getAsString(json, "central_item");
        Item centralItem = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(centralStr));
        if (centralItem == null) return;

        JsonArray inputArray = GsonHelper.getAsJsonArray(json, "input_items");
        ItemMultiSet inputItems = new ItemMultiSet();

        for (JsonElement element : inputArray) {
            String itemStr = element.getAsString();
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(itemStr));
            if (item != null) inputItems.add(item);
        }

        String resultStr = GsonHelper.getAsString(json, "result");
        Item result = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(resultStr));

        if (result != null) {
            AngelRitualRecipe recipe = new AngelRitualRecipe(centralItem, inputItems);
            AngelRitualHelper.addAngelRitualRecipe(recipe, result);
        }
    }
}
