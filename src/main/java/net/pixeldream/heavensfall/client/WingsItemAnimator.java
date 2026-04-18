package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.common.animation.controller.AzAnimationController;
import mod.azure.azurelib.common.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.common.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pixeldream.heavensfall.HeavensFallMod;
import org.jetbrains.annotations.NotNull;

public class WingsItemAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "animations/angel_wings.animation.json"
    );

    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller")
                        .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }

}
