package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.HFItems;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class HFItemTagProvider extends ItemTagsProvider {
    public HFItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                               CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, HeavensFallMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(Tags.Items.ARMORS)
                .add(HFItems.CRIMSON_HELMET.get())
                .add(HFItems.CRIMSON_CHESPLATE.get())
                .add(HFItems.CRIMSON_LEGGINGS.get())
                .add(HFItems.CRIMSON_BOOTS.get())
                .add(HFItems.FALLEN_HELMET.get())
                .add(HFItems.FALLEN_CHESPLATE.get())
                .add(HFItems.FALLEN_LEGGINGS.get())
                .add(HFItems.FALLEN_BOOTS.get())
                .add(HFItems.ARCLIGHT_HELMET.get())
                .add(HFItems.ARCLIGHT_CHESPLATE.get())
                .add(HFItems.ARCLIGHT_LEGGINGS.get())
                .add(HFItems.ARCLIGHT_BOOTS.get());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(HFItems.CRIMSON_HELMET.get())
                .add(HFItems.FALLEN_HELMET.get())
                .add(HFItems.ARCLIGHT_HELMET.get());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(HFItems.CRIMSON_CHESPLATE.get())
                .add(HFItems.FALLEN_CHESPLATE.get())
                .add(HFItems.ARCLIGHT_CHESPLATE.get());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(HFItems.CRIMSON_LEGGINGS.get())
                .add(HFItems.FALLEN_LEGGINGS.get())
                .add(HFItems.ARCLIGHT_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(HFItems.CRIMSON_BOOTS.get())
                .add(HFItems.FALLEN_BOOTS.get())
                .add(HFItems.ARCLIGHT_BOOTS.get());

        tag(ItemTags.SWORDS)
                .add(HFItems.HOLY_GREATSWORD.get());

        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(HFItems.CRIMSON_HELMET.get())
                .add(HFItems.CRIMSON_CHESPLATE.get())
                .add(HFItems.CRIMSON_LEGGINGS.get())
                .add(HFItems.CRIMSON_BOOTS.get())
                .add(HFItems.FALLEN_HELMET.get())
                .add(HFItems.FALLEN_CHESPLATE.get())
                .add(HFItems.FALLEN_LEGGINGS.get())
                .add(HFItems.FALLEN_BOOTS.get())
                .add(HFItems.ARCLIGHT_HELMET.get())
                .add(HFItems.ARCLIGHT_CHESPLATE.get())
                .add(HFItems.ARCLIGHT_LEGGINGS.get())
                .add(HFItems.ARCLIGHT_BOOTS.get());

    }

}
