package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class ArclightArmorRenderer extends AzArmorRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/armor/arclight_paladin_armor.geo.json"
    );

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/armor/arclight_paladin_armor.png"
    );

    public ArclightArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setAnimatorProvider(ArclightArmorAnimator::new)
                        //.setBoneProvider(new ArclightBoneProvider())
                        .build()
        );
    }
}