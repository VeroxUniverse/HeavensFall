package net.pixeldream.heavensfall.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.recipes.ritual.AngelRitualRecipe;
import net.pixeldream.heavensfall.recipes.ritual.DemonRitualRecipe;

public class HFRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, HeavensFallMod.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, HeavensFallMod.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DemonRitualRecipe>> DEMON_RITUAL_TYPE =
            TYPES.register("demon_ritual", () -> RecipeType.register("demon_ritual"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DemonRitualRecipe>> DEMON_RITUAL_SERIALIZER =
            SERIALIZERS.register("demon_ritual", () -> new RitualRecipeSerializer<>(DemonRitualRecipe::new));

    public static final DeferredHolder<RecipeType<?>, RecipeType<AngelRitualRecipe>> ANGEL_RITUAL_TYPE =
            TYPES.register("angel_ritual", () -> RecipeType.register("angel_ritual"));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AngelRitualRecipe>> ANGEL_RITUAL_SERIALIZER =
            SERIALIZERS.register("angel_ritual", () -> new RitualRecipeSerializer<>(AngelRitualRecipe::new));
}