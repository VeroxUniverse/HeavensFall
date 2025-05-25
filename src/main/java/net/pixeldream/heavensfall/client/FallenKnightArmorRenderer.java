package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class FallenKnightArmorRenderer extends AzArmorRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/armor/fallen_knight_armor.geo.json"
    );

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/armor/fallen_knight_armor.png"
    );

    public FallenKnightArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setAnimatorProvider(FallenKnightAnimator::new)
//                        .setBoneProvider(new ArclightBoneProvider())
                        .addRenderLayer(new AzAutoGlowingLayer<>())
                        .build()
        );
    }
}