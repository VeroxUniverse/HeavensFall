package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

public class HolyKnightArmorRenderer extends AzArmorRenderer {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "geo/armor/holy_knight_armor.geo.json"
    );

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "textures/armor/holy_knight_armor.png"
    );

    public HolyKnightArmorRenderer() {
        super(
                AzArmorRendererConfig.builder(MODEL, TEXTURE)
                        .setAnimatorProvider(ArmorAnimator::new)
                        //.setBoneProvider(new ArclightBoneProvider())
                        .build()
        );
    }
}