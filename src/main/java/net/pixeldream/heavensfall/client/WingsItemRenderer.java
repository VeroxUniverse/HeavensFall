package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.Heavensfall;

public class WingsItemRenderer extends AzArmorRenderer {
    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            Heavensfall.MODID,
            "geo/wings/angel_wings.geo.json"
    );

    public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            Heavensfall.MODID,
            "textures/models/wings/angel_wings.png"
    );

    public WingsItemRenderer() {
        super(
                AzItemRendererConfig.builder(GEO, TEX)
                        .setAnimatorProvider(WingsItemAnimator::new)
                        .build()
        );
    }
}