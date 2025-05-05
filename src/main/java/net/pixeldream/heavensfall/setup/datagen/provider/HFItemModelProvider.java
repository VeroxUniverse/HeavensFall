package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.HFItems;

public class HFItemModelProvider extends ItemModelProvider {
    public HFItemModelProvider(PackOutput output, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(output, HeavensFallMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(HFItems.ARCLIGHT_HELMET.get());
        basicItem(HFItems.ARCLIGHT_CHESPLATE.get());
        basicItem(HFItems.ARCLIGHT_LEGGINGS.get());
        basicItem(HFItems.ARCLIGHT_BOOTS.get());

    }
}