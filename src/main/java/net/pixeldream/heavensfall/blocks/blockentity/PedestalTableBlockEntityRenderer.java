package net.pixeldream.heavensfall.blocks.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class PedestalTableBlockEntityRenderer implements BlockEntityRenderer<PedestalTableBlockEntity> {
    public PedestalTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PedestalTableBlockEntity pedestal, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = pedestal.getHeldItem();

        if (stack == null || stack.isEmpty()) return;

        BlockPos startPos = pedestal.getBlockPos();
        BlockPos altarPos = pedestal.getAltarPos();

        if (altarPos == null || pedestal.getAnimationStep() >= PedestalTableBlockEntity.TOTAL_ANIMATION_STEPS) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 1.05f, 0.5f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(pedestal.getRenderingRotation()));
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                    getLightLevel(pedestal.getLevel(), startPos), OverlayTexture.NO_OVERLAY,
                    poseStack, bufferSource, pedestal.getLevel(), 1);
            poseStack.popPose();
            return;
        }

        int step = pedestal.getAnimationStep();
        int totalSteps = PedestalTableBlockEntity.TOTAL_ANIMATION_STEPS;
        double t = step / (double) totalSteps;

        double startX = startPos.getX() + 0.5;
        double startY = startPos.getY() + 1.15;
        double startZ = startPos.getZ() + 0.5;

        double endX = altarPos.getX() + 0.5;
        double endY = altarPos.getY() + 1.15;
        double endZ = altarPos.getZ() + 0.5;

        double x, y, z;

        if (t < 0.25) {
            x = startX;
            y = startY + 1.5 * (t / 0.25);
            z = startZ;
        } else if (t < 0.75) {
            double phaseT = (t - 0.25) / 0.5;
            x = startX + (endX - startX) * phaseT;
            y = startY + 1.5;
            z = startZ + (endZ - startZ) * phaseT;
        } else {
            if (t < 0.9) {
                x = endX;
                y = startY + 1.5;
                z = endZ;
            } else {
                double fallT = (t - 0.9) / 0.1;
                x = endX;
                y = (startY + 1.5) * (1 - fallT) + endY * fallT;
                z = endZ;
            }
        }

        poseStack.pushPose();
        poseStack.translate(x - startPos.getX(), y - startPos.getY(), z - startPos.getZ());
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(pedestal.getRenderingRotation()));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(pedestal.getLevel(), startPos), OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, pedestal.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}

