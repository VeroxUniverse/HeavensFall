package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.worldgen.HFBiomeModifiers;
import net.pixeldream.heavensfall.worldgen.features.HFConfiguredFeatures;
import net.pixeldream.heavensfall.worldgen.features.HFPlacedFeatures;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HFWorldGenProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, HFConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, HFPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, HFBiomeModifiers::bootstrap);

    public HFWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(HeavensFallMod.MODID));
    }
}