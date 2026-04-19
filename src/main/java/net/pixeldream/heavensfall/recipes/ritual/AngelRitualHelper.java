package net.pixeldream.heavensfall.recipes.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pixeldream.heavensfall.blocks.blockentity.AngelPedestalBlockEntity;
import net.pixeldream.heavensfall.recipes.HFRecipes;
import org.joml.Vector3f;

import java.util.*;

public class AngelRitualHelper {
    public static final DustParticleOptions GOLD_DUST = new DustParticleOptions(new Vector3f(0.96f, 0.74f, 0.13f), 1.0f);

    public static final Set<Block> CANDLE_BLOCKS = Set.of(
            Blocks.WHITE_CANDLE, Blocks.ORANGE_CANDLE, Blocks.MAGENTA_CANDLE,
            Blocks.LIGHT_BLUE_CANDLE, Blocks.YELLOW_CANDLE, Blocks.LIME_CANDLE,
            Blocks.PINK_CANDLE, Blocks.GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE,
            Blocks.CYAN_CANDLE, Blocks.PURPLE_CANDLE, Blocks.BLUE_CANDLE,
            Blocks.BROWN_CANDLE, Blocks.GREEN_CANDLE, Blocks.RED_CANDLE,
            Blocks.BLACK_CANDLE, Blocks.CANDLE
    );

    public static Optional<AngelRitualRecipe> getMatchingRecipe(Level level, BlockPos centralPos, ItemStack stack) {
        List<BlockEntity> pedestals = getSurroundingPedestals(centralPos, level);
        ItemMultiSet pedestalItems = getItemsFromPedestals(pedestals);

        return level.getRecipeManager().getAllRecipesFor(HFRecipes.ANGEL_RITUAL_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe.matches(stack.getItem(), pedestalItems))
                .findFirst();
    }

    public static boolean isValidRecipe(Level level, BlockPos centralPos, ItemStack stack) {
        if (!hasRequiredEnvironment(level, centralPos)) return false;
        if (getSurroundingPedestals(centralPos, level).size() < 4) return false;
        return getMatchingRecipe(level, centralPos, stack).isPresent();
    }

    public static boolean hasRequiredEnvironment(Level level, BlockPos center) {
        int candleCount = 0;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    checkPos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    BlockState state = level.getBlockState(checkPos);
                    if (CANDLE_BLOCKS.contains(state.getBlock())) candleCount++;
                }
            }
        }
        return candleCount >= 8;
    }

    public static List<BlockEntity> getSurroundingPedestals(BlockPos origin, Level level) {
        BlockPos[] offsets = { origin.north(2), origin.south(2), origin.east(2), origin.west(2) };
        List<BlockEntity> pedestals = new ArrayList<>();
        for (BlockPos pos : offsets) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof AngelPedestalBlockEntity pedestal && !pedestal.getHeldItem().isEmpty()) {
                pedestals.add(pedestal);
            }
        }
        return pedestals;
    }

    public static void spawnSmokeAtPedestals(Level level, BlockPos altarPos) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        for (BlockEntity be : getSurroundingPedestals(altarPos, level)) {
            if (be instanceof AngelPedestalBlockEntity pedestal) {
                BlockPos pPos = pedestal.getBlockPos();
                serverLevel.sendParticles(GOLD_DUST,
                        pPos.getX() + 0.5, pPos.getY() + 1.2, pPos.getZ() + 0.5,
                        7, 0.15, 0.15, 0.15, 0.05);
                serverLevel.sendParticles(ParticleTypes.ENCHANT,
                        pPos.getX() + 0.5, pPos.getY() + 1.2, pPos.getZ() + 0.5,
                        3, 0.1, 0.1, 0.1, 0.1);
            }
        }
    }

    public static void spawnRitualCompletionParticles(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(ParticleTypes.FLASH,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                1, 0, 0, 0, 0);
        serverLevel.sendParticles(GOLD_DUST,
                pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                50, 0.5, 1.0, 0.5, 0.1);
    }

    public static void spawnAngelRitualStartEffect(ServerLevel level, BlockPos pos) {
        Random random = new Random();
        DustParticleOptions dust = new DustParticleOptions(new Vector3f(1.0f, 0.9f, 0.6f), 1.0f);
        for (int i = 0; i < 50; i++) {
            level.sendParticles(dust, pos.getX() + 0.5 + random.nextGaussian() * 0.5, pos.getY() + random.nextDouble() * 0.5 + 1.0, pos.getZ() + 0.5 + random.nextGaussian() * 0.5, 1, 0, 0, 0, 0);
        }
    }

    public static void clearItemsFromPedestals(Level level, BlockPos pos) {
        for (BlockEntity pedestal : getSurroundingPedestals(pos, level)) {
            if (pedestal instanceof AngelPedestalBlockEntity p) {
                p.clearContents();
                level.sendBlockUpdated(p.getBlockPos(), p.getBlockState(), p.getBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    public static ItemMultiSet getItemsFromPedestals(List<BlockEntity> surroundingPedestals) {
        ItemMultiSet pedestalItems = new ItemMultiSet();
        for (BlockEntity pedestal : surroundingPedestals) {
            if (pedestal instanceof AngelPedestalBlockEntity statue) {
                pedestalItems.add(statue.getHeldItem().getItem());
            }
        }
        return pedestalItems;
    }

    public static Item getResultForRecipe(Level level, BlockPos center, ItemStack stack) {
        return getMatchingRecipe(level, center, stack)
                .map(recipe -> recipe.getResultItem(level.registryAccess()).getItem())
                .orElse(Items.AIR);
    }
}