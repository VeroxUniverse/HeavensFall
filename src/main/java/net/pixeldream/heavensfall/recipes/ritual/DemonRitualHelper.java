package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.blockentity.PedestalBlockEntity;
import org.joml.Vector3f;

import java.util.*;

public class DemonRitualHelper {
    private static Map<DemonRitualRecipe, Item> ritualRecipes;
    private static Map<Item, ParticleOptions> resultToParticle;
    private static Map<Item, Vector3f> resultToColor;

    public static final DustParticleOptions BLACK_DUST = new DustParticleOptions(
            new Vector3f(0f, 0f, 0f), 1.0f);

    private static final Set<Block> CANDLE_BLOCKS = Set.of(
            Blocks.WHITE_CANDLE, Blocks.ORANGE_CANDLE, Blocks.MAGENTA_CANDLE,
            Blocks.LIGHT_BLUE_CANDLE, Blocks.YELLOW_CANDLE, Blocks.LIME_CANDLE,
            Blocks.PINK_CANDLE, Blocks.GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE,
            Blocks.CYAN_CANDLE, Blocks.PURPLE_CANDLE, Blocks.BLUE_CANDLE,
            Blocks.BROWN_CANDLE, Blocks.GREEN_CANDLE, Blocks.RED_CANDLE,
            Blocks.BLACK_CANDLE, Blocks.CANDLE
    );

    public static boolean isValidRecipe(Level level, BlockPos centralPos, ItemStack stack) {
        if (!hasRequiredEnvironment(level, centralPos)) return false;
        Map<DemonRitualRecipe, Item> recipes = DemonRitualHelper.getDemonRitualRecipes();
        List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centralPos, level);
        if (surroundingPedestals.size() < 4) return false;

        ItemMultiSet pedestalItems = getItemsFromPedestals(surroundingPedestals);
        DemonRitualRecipe recipe = new DemonRitualRecipe(stack.getItem(), pedestalItems);
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

    public static boolean hasRequiredEnvironment(Level level, BlockPos center) {
        int candleCount = 0;
        int skullCount = 0;

        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    checkPos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    if (CANDLE_BLOCKS.contains(block)) {
                        candleCount++;
                    } else if (block == Blocks.SKELETON_SKULL || block == Blocks.SKELETON_WALL_SKULL) {
                        skullCount++;
                    }
                }
            }
        }

        return candleCount >= 6 && skullCount >= 2;
    }

    public static void spawnItemTrailParticles(Level level, BlockPos altarPos) {
        if (!(level instanceof ServerLevel server)) return;

        for (BlockEntity be : getSurroundingPedestals(altarPos, level)) {
            if (be instanceof PedestalBlockEntity pedestal) {
                Vec3 pos = pedestal.getCurrentRenderItemPosition(0);
                server.sendParticles(BLACK_DUST, pos.x, pos.y + 0.05, pos.z,
                        1, 0.01, 0.01, 0.01, 0.001);
            }
        }
    }

    public static void spawnExplosionDust(ServerLevel server, BlockPos center, float radius, int count) {
        double cx = center.getX() + 0.5;
        double cy = center.getY() + 1.2;
        double cz = center.getZ() + 0.5;

        for (int i = 0; i < count; i++) {
            double theta = server.random.nextDouble() * 2 * Math.PI;
            double phi = Math.acos(2 * server.random.nextDouble() - 1);
            double r = server.random.nextDouble() * radius;

            double dx = r * Math.sin(phi) * Math.cos(theta);
            double dy = r * Math.cos(phi);
            double dz = r * Math.sin(phi) * Math.sin(theta);

            server.sendParticles(BLACK_DUST,
                    cx + dx, cy + dy, cz + dz,
                    0, 0, 0, 0, 0.01);
        }
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
        Map<DemonRitualRecipe, Item> recipes = getDemonRitualRecipes();
        List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centerPos, level);
        ItemMultiSet pedestalItems = getItemsFromPedestals(surroundingPedestals);
        DemonRitualRecipe recipe = new DemonRitualRecipe(stack.getItem(), pedestalItems);
        Item result = recipes.get(recipe);
        return result;
    }

    public static void addDemonRitualRecipe(DemonRitualRecipe recipe, Item result) {
        if (ritualRecipes == null) {
            initializeRecipes();
        }

        if (result == null) {
            HeavensFallMod.LOGGER.error("Attempted to register a ritual recipe with null result item!");
            return;
        }

        for (Map.Entry<DemonRitualRecipe, Item> entry : ritualRecipes.entrySet()) {
            if (entry.getKey().equals(recipe)) {
                HeavensFallMod.LOGGER.warn("Duplicate recipe found! Existing result: {}, New result: {}", entry.getValue(), result);
                return;
            }
        }

        resultToColor.put(result, new Vector3f(1.0f, 0.0f, 0.0f));
        ritualRecipes.put(recipe, result);
        HeavensFallMod.LOGGER.debug("Registered ritual recipe: Central={}, Inputs={}, Result={}", recipe.getCentralItem(), recipe.getInputItems(), result);
    }

    public static void initializeRecipes() {
        HeavensFallMod.LOGGER.info("Initializing ritual recipes");
        ritualRecipes = new HashMap<>();
        resultToParticle = new HashMap<>();
        resultToColor = new HashMap<>();
    }

    public static Map<DemonRitualRecipe, Item> getDemonRitualRecipes() {
        if (ritualRecipes == null) {
            initializeRecipes();
        }
        return ritualRecipes;
    }

    public static Set<Item> getAllResultItems() {
        return new HashSet<>(ritualRecipes.values());
    }

    public static void debugPrintAllRecipes() {
        HeavensFallMod.LOGGER.info("=== DEBUG: All Registered Ritual Recipes ===");
        if (ritualRecipes == null || ritualRecipes.isEmpty()) {
            HeavensFallMod.LOGGER.info("No recipes registered!");
            return;
        }

        int count = 0;
        for (Map.Entry<DemonRitualRecipe, Item> entry : ritualRecipes.entrySet()) {
            DemonRitualRecipe recipe = entry.getKey();
            Item result = entry.getValue();
            HeavensFallMod.LOGGER.info("Recipe #{}: Central={}, Inputs={}, Result={}", count++, recipe.getCentralItem(), recipe.getInputItems(), result);
        }
        HeavensFallMod.LOGGER.info("=== END DEBUG ===");
    }
}
