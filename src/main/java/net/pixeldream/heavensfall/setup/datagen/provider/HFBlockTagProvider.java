package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.blocks.HFBlocks;
import net.pixeldream.heavensfall.util.HFTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class HFBlockTagProvider extends BlockTagsProvider {
    public HFBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HeavensFallMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(HFBlocks.RAW_CHALK_BLOCK.get());

        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(HFBlocks.ALTAR_BLOCK.get())
                .add(HFBlocks.PEDESTAL_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(HFBlocks.ALTAR_BLOCK.get())
                .add(HFBlocks.PEDESTAL_BLOCK.get());

        this.tag(HFTags.Blocks.RIVER_REPLACEABLES)
                .add(Blocks.SAND)
                .add(Blocks.DIRT)
                .add(Blocks.CLAY)
                .add(Blocks.GRAVEL);

    }

    @Override
    public String getName() {
        return "Block Tags";
    }

}
