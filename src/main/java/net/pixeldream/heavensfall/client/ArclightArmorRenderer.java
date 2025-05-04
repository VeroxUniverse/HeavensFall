package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class ArclightArmorRenderer extends AzArmorRenderer {
    private static final ResourceLocation GEO = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/armor/arclight_paladin_armor.geo.json"
    );

    public static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/armors/arclight_paladin_armor.png"
    );

    public ArclightArmorRenderer() {
        super(
                AzItemRendererConfig.builder(GEO, TEX).build());
    }
}