package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.blockentity.PedestalTableBlockEntity;
import org.joml.Vector3f;

import java.util.*;

public class AngelRitualHelper {
    private static Map<AngelRitualRecipe, Item> ritualRecipes;
    private static Map<Item, ParticleOptions> resultToParticle;
    private static Map<Item, Vector3f> resultToColor;

    public static final Set<Block> CANDLE_BLOCKS = Set.of(
            Blocks.WHITE_CANDLE, Blocks.ORANGE_CANDLE, Blocks.MAGENTA_CANDLE,
            Blocks.LIGHT_BLUE_CANDLE, Blocks.YELLOW_CANDLE, Blocks.LIME_CANDLE,
            Blocks.PINK_CANDLE, Blocks.GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE,
            Blocks.CYAN_CANDLE, Blocks.PURPLE_CANDLE, Blocks.BLUE_CANDLE,
            Blocks.BROWN_CANDLE, Blocks.GREEN_CANDLE, Blocks.RED_CANDLE,
            Blocks.BLACK_CANDLE, Blocks.CANDLE
    );

    public static boolean isValidRecipe(Level level, BlockPos centralPos, ItemStack stack) {
        if (!hasRequiredEnvironment(level, centralPos)) return false;
        Map<AngelRitualRecipe, Item> recipes = AngelRitualHelper.getAngelRitualRecipes();
        List<BlockEntity> surroundingPedestals = getSurroundingPedestals(centralPos, level);
        if (surroundingPedestals.size() < 4) return false;

        ItemMultiSet pedestalItems = getItemsFromPedestals(surroundingPedestals);
        AngelRitualRecipe recipe = new AngelRitualRecipe(stack.getItem(), pedestalItems);
        return recipes.get(recipe) != null;
    }

    public static boolean hasRequiredEnvironment(Level level, BlockPos center) {
        int candleCount = 0;

        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    checkPos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    if (CANDLE_BLOCKS.contains(block)) {
                        candleCount++;
                    }
                }
            }
        }

        return candleCount >= 8;
    }

    public static void spawnSmokeAtPedestals(Level level, BlockPos altarPos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos[] offsets = {
                altarPos.north(2),
                altarPos.south(2),
                altarPos.east(2),
                altarPos.west(2)
        };

        for (BlockPos pedestalPos : offsets) {
            BlockEntity be = level.getBlockEntity(pedestalPos);
            if (be instanceof PedestalTableBlockEntity pedestal) {
                ItemStack stack = pedestal.getHeldItem();
                Vector3f color = getColorForItem(stack.getItem());
                DustParticleOptions dust = new DustParticleOptions(color, 1.0f);

            }
        }
    }

    public static void spawnRitualCompletionParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            double offsetX = (random.nextDouble() - 0.5);
            double offsetY = random.nextDouble();
            double offsetZ = (random.nextDouble() - 0.5);
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 1.0 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1, 0, 0, 0, 0);
        }

        Vector3f goldColor = new Vector3f(1.0f, 0.9f, 0.2f);
        DustParticleOptions dust = new DustParticleOptions(goldColor, 1.0f);
        for (int i = 0; i < 30; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.6;
            double offsetY = random.nextDouble();
            double offsetZ = (random.nextDouble() - 0.5) * 0.6;
            serverLevel.sendParticles(dust,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 1.0 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }

    public static void spawnAngelRitualStartEffect(ServerLevel level, BlockPos pos) {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            double offsetX = random.nextGaussian() * 0.5;
            double offsetY = random.nextDouble() * 0.5 + 1.0;
            double offsetZ = random.nextGaussian() * 0.5;
            Vector3f color = new Vector3f(1.0f, 0.9f, 0.6f); // gold-ish
            DustParticleOptions dust = new DustParticleOptions(color, 1.0f);
            level.sendParticles(dust,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1, 0, 0, 0, 0);
        }
    }

    public static Vector3f getColorForItem(Item item) {
        return resultToColor.getOrDefault(item, new Vector3f(0.0f, 0.0f, 0.0f));
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
            if (entity instanceof PedestalTableBlockEntity pedestal && !pedestal.getHeldItem().isEmpty()) {
                pedestals.add(pedestal);
            }
        }

        return pedestals;
    }

    public static void clearItemsFromPedestals(Level level, BlockPos pos) {
        for (BlockEntity pedestal : getSurroundingPedestals(pos, level)) {
            if (pedestal instanceof PedestalTableBlockEntity p) {
                BlockState prevState = p.getBlockState();
                p.setHeldItem(ItemStack.EMPTY);
                level.sendBlockUpdated(p.getBlockPos(), prevState, p.getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public static ItemMultiSet getItemsFromPedestals(List<BlockEntity> surroundingPedestals) {
        ItemMultiSet pedestalItems = new ItemMultiSet();

        for (BlockEntity pedestal : surroundingPedestals) {
            if (pedestal instanceof PedestalTableBlockEntity statue) {
                pedestalItems.add(statue.getHeldItem().getItem());
            }
        }
        return pedestalItems;
    }

    public static Item getResultForRecipe(Level level, BlockPos center, ItemStack stack) {
        List<BlockEntity> pedestals = getSurroundingPedestals(center, level);
        ItemMultiSet inputs = getItemsFromPedestals(pedestals);
        AngelRitualRecipe recipe = new AngelRitualRecipe(stack.getItem(), inputs);
        return ritualRecipes.get(recipe);
    }

    public static void addAngelRitualRecipe(AngelRitualRecipe recipe, Item result) {
        if (ritualRecipes == null) {
            initializeRecipes();
        }

        if (result == null) {
            HeavensFallMod.LOGGER.error("Attempted to register a ritual recipe with null result item!");
            return;
        }

        for (Map.Entry<AngelRitualRecipe, Item> entry : ritualRecipes.entrySet()) {
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

    public static Map<AngelRitualRecipe, Item> getAngelRitualRecipes() {
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
        return new Vector3f(1, 1, 1);
    }

    public static void debugPrintAllRecipes() {
        HeavensFallMod.LOGGER.info("=== DEBUG: All Registered Ritual Recipes ===");
        if (ritualRecipes == null || ritualRecipes.isEmpty()) {
            HeavensFallMod.LOGGER.info("No recipes registered!");
            return;
        }

        int count = 0;
        for (Map.Entry<AngelRitualRecipe, Item> entry : ritualRecipes.entrySet()) {
            AngelRitualRecipe recipe = entry.getKey();
            Item result = entry.getValue();
            HeavensFallMod.LOGGER.info("Recipe #{}: Central={}, Inputs={}, Result={}", count++, recipe.getCentralItem(), recipe.getInputItems(), result);
        }
        HeavensFallMod.LOGGER.info("=== END DEBUG ===");
    }
}

