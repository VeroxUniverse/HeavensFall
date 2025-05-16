package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.blockentity.PedestalBlockEntity;
import org.joml.Vector3f;

import java.util.*;

public class RitualHelper {
    private static Map<RitualRecipe, Item> ritualRecipes;
    private static Map<Item, ParticleOptions> resultToParticle;
    private static Map<Item, Vector3f> resultToColor;

    public static boolean isValidRecipe(Level level, BlockPos centralPos, ItemStack stack) {
        Map<RitualRecipe, Item> recipes = RitualHelper.getRitualRecipes();
        List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centralPos, level);
        if (surroundingPedestals.size() < 4) return false;

        ItemMultiSet pedestalItems = getItemsFromPedestals(surroundingPedestals);
        RitualRecipe recipe = new RitualRecipe(stack.getItem(), pedestalItems);
        return recipes.get(recipe) != null;
    }

    public static List<BlockEntity> getSurroundingPedestals(BlockPos origin, Level level) {
        BlockPos[] offsets = {
                origin.north(2),
                origin.south(2),
                origin.east(2),
                origin.west(2)
        };

        List<BlockEntity> pedestals = new ArrayList<>();

        for (BlockPos pos : offsets) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof PedestalBlockEntity pedestal && !pedestal.getHeldItem().isEmpty()) {
                pedestals.add(pedestal);
            }
        }
        return pedestals;
    }

    public static void spawnSmokeAtPedestals(Level level, BlockPos origin) {
        if (!level.isClientSide()) {
            BlockPos[] offsets = {
                    origin.north(2),
                    origin.south(2),
                    origin.east(2),
                    origin.west(2)
            };

            for (BlockPos pos : offsets) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PedestalBlockEntity) {
                 spawnPoof(level, pos);
                }
            }
        }
    }

    public static void spawnPoof(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        ((ServerLevel) level).sendParticles(
                ParticleTypes.POOF,
                pos.getX() + 0.5,
                pos.getY() + 1.2,
                pos.getZ() + 0.5,
                6,
                0.1, 0.1, 0.1,
                0.01
        );
    }

    public static ItemMultiSet getItemsFromPedestals(List<BlockEntity> surroundingPedestals) {
        ItemMultiSet pedestalItems = new ItemMultiSet();

        for (BlockEntity pedestal : surroundingPedestals) {
            if (pedestal instanceof PedestalBlockEntity statue) {
                pedestalItems.add(statue.getHeldItem().getItem());
            }
        }

        return pedestalItems;
    }

    public static void clearItemsFromPedestals(Level level, BlockPos pos) {
        for (BlockEntity pedestal : getSurroundingPedestals(pos, level)) {
            if (pedestal instanceof PedestalBlockEntity statue) {
                BlockState prevState = statue.getBlockState();
                statue.setHeldItem(ItemStack.EMPTY);
                BlockState nextState = statue.getBlockState();
                level.sendBlockUpdated(statue.getBlockPos(), prevState, nextState, Block.UPDATE_CLIENTS);
            }
        }
    }

    public static Item getResultForRecipe(Level level, BlockPos centerPos, ItemStack stack) {
        Map<RitualRecipe, Item> recipes = getRitualRecipes();
        List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centerPos, level);
        ItemMultiSet pedestalItems = getItemsFromPedestals(surroundingPedestals);
        RitualRecipe recipe = new RitualRecipe(stack.getItem(), pedestalItems);
        Item result = recipes.get(recipe);

        if (result == null) {
            HeavensFallMod.LOGGER.warn("No result found for ritual with central item {} and inputs {}",
                    stack.getItem(), pedestalItems);
        }

        return result;
    }

    public static void addRitualRecipe(RitualRecipe recipe, Item result) {
        if (ritualRecipes == null) {
            initializeRecipes();
        }

        if (result == null) {
            HeavensFallMod.LOGGER.error("Attempted to register a ritual recipe with null result item!");
            return;
        }

        for (Map.Entry<RitualRecipe, Item> entry : ritualRecipes.entrySet()) {
            if (entry.getKey().equals(recipe)) {
                HeavensFallMod.LOGGER.warn("Duplicate recipe found! Existing result: {}, New result: {}", entry.getValue(), result);
                return;
            }
        }

        ritualRecipes.put(recipe, result);
        HeavensFallMod.LOGGER.debug("Registered ritual recipe: Central={}, Inputs={}, Result={}", recipe.getCentralItem(), recipe.getInputItems(), result);
    }

    public static void initializeRecipes() {
        HeavensFallMod.LOGGER.info("Initializing ritual recipes");
        ritualRecipes = new HashMap<>();
        resultToParticle = new HashMap<>();
        resultToColor = new HashMap<>();
    }

    public static Map<RitualRecipe, Item> getRitualRecipes() {
        if (ritualRecipes == null) {
            initializeRecipes();
        }
        return ritualRecipes;
    }

    public static ParticleOptions getParticleForItem(Item item) {
        return resultToParticle.getOrDefault(item, ParticleTypes.ENCHANT);
    }

    public static Vector3f getColorForParticles(ParticleOptions particles) {
        for (Map.Entry<Item, ParticleOptions> entry : resultToParticle.entrySet()) {
            if (entry.getValue().equals(particles)) {
                return resultToColor.getOrDefault(entry.getKey(), new Vector3f(1, 1, 1));
            }
        }
        return new Vector3f(1, 1, 1); // Fallback: weiß
    }

    public static void debugPrintAllRecipes() {
        HeavensFallMod.LOGGER.info("=== DEBUG: All Registered Ritual Recipes ===");
        if (ritualRecipes == null || ritualRecipes.isEmpty()) {
            HeavensFallMod.LOGGER.info("No recipes registered!");
            return;
        }

        int count = 0;
        for (Map.Entry<RitualRecipe, Item> entry : ritualRecipes.entrySet()) {
            RitualRecipe recipe = entry.getKey();
            Item result = entry.getValue();
            HeavensFallMod.LOGGER.info("Recipe #{}: Central={}, Inputs={}, Result={}", count++, recipe.getCentralItem(), recipe.getInputItems(), result);
        }
        HeavensFallMod.LOGGER.info("=== END DEBUG ===");
    }
}
