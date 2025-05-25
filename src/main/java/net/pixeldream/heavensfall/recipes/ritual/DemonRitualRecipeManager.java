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

public class DemonRitualRecipeManager extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public DemonRitualRecipeManager() {
        super(GSON, "ritual_recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManager, ProfilerFiller profiler) {
        HeavensFallMod.LOGGER.info("Loading ritual recipes...");
        DemonRitualHelper.initializeRecipes();

        resourceList.forEach((location, jsonElement) -> {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(jsonElement, "ritual recipe");
                loadRitualRecipe(location, json);
            } catch (Exception e) {
                HeavensFallMod.LOGGER.error("Error loading ritual recipe {}: {}", location, e.getMessage());
                e.printStackTrace();
            }
        });

        DemonRitualHelper.debugPrintAllRecipes();
        HeavensFallMod.LOGGER.info("Loaded {} ritual recipes", DemonRitualHelper.getDemonRitualRecipes().size());
    }

    private void loadRitualRecipe(ResourceLocation location, JsonObject json) {
        HeavensFallMod.LOGGER.debug("Loading ritual recipe from {}", location);

        String centralItemId = GsonHelper.getAsString(json, "central_item");
        String[] centralItemParts = centralItemId.split(":", 2);
        String centralItemNamespace = centralItemParts.length > 1 ? centralItemParts[0] : "minecraft";
        String centralItemPath = centralItemParts.length > 1 ? centralItemParts[1] : centralItemParts[0];
        ResourceLocation centralItemRL = ResourceLocation.fromNamespaceAndPath(centralItemNamespace, centralItemPath);
        Item centralItem = BuiltInRegistries.ITEM.get(centralItemRL);

        if (centralItem == null) {
            HeavensFallMod.LOGGER.error("Unknown central item: {}", centralItemId);
            return;
        }

        JsonArray inputItemsJson = GsonHelper.getAsJsonArray(json, "input_items");
        ItemMultiSet inputItems = new ItemMultiSet();

        HeavensFallMod.LOGGER.debug("Recipe {} has {} input items", location, inputItemsJson.size());

        for (int i = 0; i < Math.min(inputItemsJson.size(), 4); i++) {
            JsonElement element = inputItemsJson.get(i);
            String itemId = element.getAsString();
            String[] parts = itemId.split(":", 2);
            String namespace = parts.length > 1 ? parts[0] : "minecraft";
            String path = parts.length > 1 ? parts[1] : parts[0];
            ResourceLocation itemRL = ResourceLocation.fromNamespaceAndPath(namespace, path);
            Item item = BuiltInRegistries.ITEM.get(itemRL);

            if (item == null) {
                HeavensFallMod.LOGGER.error("Unknown input item: {}", itemId);
                return;
            }

            inputItems.add(item);
            HeavensFallMod.LOGGER.debug("Added input item {} to recipe {}", itemId, location);
        }

        String resultItemId = GsonHelper.getAsString(json, "result");
        HeavensFallMod.LOGGER.debug("Recipe {} has result {}", location, resultItemId);

        String[] resultParts = resultItemId.split(":", 2);
        String resultNamespace = resultParts.length > 1 ? resultParts[0] : "minecraft";
        String resultPath = resultParts.length > 1 ? resultParts[1] : resultParts[0];
        ResourceLocation resultItemRL = ResourceLocation.fromNamespaceAndPath(resultNamespace, resultPath);

        Item resultItem = BuiltInRegistries.ITEM.get(resultItemRL);
        if (resultItem == null) {
            HeavensFallMod.LOGGER.error("Unknown or missing result item: {}", resultItemId);
            HeavensFallMod.LOGGER.error("ResourceLocation was: {}", resultItemRL);
            return;
        }

        HeavensFallMod.LOGGER.debug("Result item {} resolved to {}", resultItemId, resultItem);

        DemonRitualRecipe recipe = new DemonRitualRecipe(centralItem, inputItems);
        DemonRitualHelper.addDemonRitualRecipe(recipe, resultItem);

        HeavensFallMod.LOGGER.debug("Registered ritual recipe {} with result {}", location, resultItemId);
    }
}