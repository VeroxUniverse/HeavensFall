package net.pixeldream.heavensfall.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.pixeldream.heavensfall.HeavensFallMod;

public class HFTags {

    public static class Blocks {
        public static final TagKey<Block> RIVER_REPLACEABLES = createTag("river_replaceables");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, name));
        }
    }

}
