package net.pixeldream.heavensfall.blocks;

import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.HFItems;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class HFBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(HeavensFallMod.MODID);

    // ALTAR //

    public static final DeferredBlock<Block> ALTAR_BLOCK = registerBlock("altar_block",
            () -> new AltarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    public static final DeferredBlock<Block> PEDESTAL_BLOCK = registerBlock("pedestal_block",
            () -> new PedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    public static final DeferredBlock<Block> ALTAR_PILLAR_BLOCK = registerBlock("angel_pillar",
            () -> new AltarPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    public static final DeferredBlock<Block> PEDESTAL_TABLE_BLOCK = registerBlock("angel_table",
            () -> new PedestalTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()));

    // BLOCKS //

    public static final DeferredBlock<Block> CHALK_BLOCK = registerBlock("chalk_block",
            () -> new ChalkBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).noOcclusion()));

    public static final DeferredBlock<Block> RAW_CHALK_BLOCK = registerBlock("raw_chalk_block",
            () -> new ColoredFallingBlock(new ColorRGBA(-8356741), BlockBehaviour.Properties.ofFullCopy(Blocks.GRAVEL)));

    public static final DeferredBlock<Block> RUNE_BLOCK = registerBlock("rune_block",
            () -> new RuneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_WIRE).noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static ToIntFunction<BlockState> litBlockEmission(int pLightValue) {
        return state -> state.getValue(BlockStateProperties.LIT) ? pLightValue : 0;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        HFItems.ITEMS_BLOCKS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
