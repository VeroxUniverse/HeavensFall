package net.pixeldream.heavensfall.client;

import mod.azure.azurelib.common.api.client.helper.ClientUtils;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationController;
import mod.azure.azurelib.rewrite.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.rewrite.animation.impl.AzItemAnimator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pixeldream.heavensfall.HeavensFallMod;
import org.jetbrains.annotations.NotNull;

public class ArclightArmorAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = ResourceLocation.fromNamespaceAndPath(
            HeavensFallMod.MODID,
            "animations/armor/empty.animation.json"
    );

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller").build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(ItemStack stack, float partialTick) {
        super.setCustomAnimations(stack, partialTick);

        var model = this.context().boneCache().getBakedModel();
        if (model == null) return;

        var capeOpt = model.getBone("cape");
        if (capeOpt.isEmpty()) return;

        var cape = capeOpt.get();
        Player player = ClientUtils.getClientPlayer();
        if (player == null) return;

        float h = partialTick;
        double dx = Mth.lerp(h, player.xCloakO, player.xCloak) - Mth.lerp(h, player.xo, player.getX());
        double dy = Mth.lerp(h, player.yCloakO, player.yCloak) - Mth.lerp(h, player.yo, player.getY());
        double dz = Mth.lerp(h, player.zCloakO, player.zCloak) - Mth.lerp(h, player.zo, player.getZ());

        float bodyRot = Mth.rotLerp(h, player.yBodyRotO, player.yBodyRot);
        double sin = Mth.sin(bodyRot * ((float) Math.PI / 180));
        double cos = -Mth.cos(bodyRot * ((float) Math.PI / 180));

        float verticalMotion = (float) dy * 10.0f;
        verticalMotion = Mth.clamp(verticalMotion, -6.0f, 32.0f);

        float capeSwayX = (float)(dx * sin + dz * cos) * 100.0f;
        capeSwayX = Mth.clamp(capeSwayX, 0.0f, 150.0f);

        float capeSwayZ = (float)(dx * cos - dz * sin) * 100.0f;
        capeSwayZ = Mth.clamp(capeSwayZ, -20.0f, 20.0f);

        if (capeSwayX < 0.0f) capeSwayX = 0.0f;

        float walkBob = Mth.lerp(h, player.oBob, player.bob);
        verticalMotion += Mth.sin(Mth.lerp(h, player.walkDistO, player.walkDist) * 6.0f) * 8.0f * walkBob;

        if (player.isCrouching()) {
            verticalMotion += 10.0f;
        }


        float baseX = 0.1f;
        float baseY = 0.0f;
        float baseZ = 0.0f;

        cape.setRotX(baseX - (capeSwayX / 200.0f + verticalMotion / 40.0f));
        cape.setRotY(baseY);
        cape.setRotZ(baseZ + (capeSwayZ / 180.0f));
    }

}

