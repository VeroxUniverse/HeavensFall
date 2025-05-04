package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class WingsItemRenderer extends AzArmorRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/armor/angel_wings.geo.json"
    );

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/armor/angel_wings.png"
    );

    public WingsItemRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setAnimatorProvider(WingsItemAnimator::new)
                        .build()
        );
    }
}