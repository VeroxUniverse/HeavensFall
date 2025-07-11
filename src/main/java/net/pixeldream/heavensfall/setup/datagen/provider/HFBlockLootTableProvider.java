package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.pixeldream.heavensfall.blocks.HFBlocks;

import java.util.Set;

public class HFBlockLootTableProvider extends BlockLootSubProvider {
    public HFBlockLootTableProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {

        this.dropSelf(HFBlocks.ALTAR_BLOCK.get());
        this.dropSelf(HFBlocks.PEDESTAL_BLOCK.get());
        this.dropSelf(HFBlocks.CHALK_BLOCK.get());
        this.dropSelf(HFBlocks.RUNE_BLOCK.get());
        this.dropSelf(HFBlocks.PEDESTAL_TABLE_BLOCK.get());

        this.add(HFBlocks.ALTAR_PILLAR_BLOCK.get(),
                block -> createDoorTable(HFBlocks.ALTAR_PILLAR_BLOCK.get()));

        this.add(HFBlocks.RAW_CHALK_BLOCK.get(),
                block -> createMultipleOreDrops(
                        HFBlocks.RAW_CHALK_BLOCK.get(),
                        HFBlocks.CHALK_BLOCK.get().asItem(),
                        1,4)
        );

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return HFBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

}