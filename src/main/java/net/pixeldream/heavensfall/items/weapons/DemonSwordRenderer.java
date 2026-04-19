package net.pixeldream.heavensfall.items.weapons;

import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class DemonSwordRenderer extends AzItemRenderer {
    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/weapon/demonic_greatsword.geo.json"
    );

    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/item/demonic_greatsword.png"
    );

    public DemonSwordRenderer() {
        super(
                AzItemRendererConfig.builder(GEO, TEX)
                        //.addRenderLayer(new AzBlockAndItemLayer<>())
                        .build()
        );
    }
}