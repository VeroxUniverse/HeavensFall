package net.pixeldream.heavensfall.setup.datagen.provider;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.pixeldream.heavensfall.Heavensfall;
import net.pixeldream.heavensfall.setup.registries.HFItems;

public class HFItemModelProvider extends ItemModelProvider {
    public HFItemModelProvider(PackOutput output, net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper) {
        super(output, Heavensfall.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(HFItems.HOLY_GREATSWORD.get());

    }
}