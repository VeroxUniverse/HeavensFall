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
import net.minecraft.world.phys.Vec3;

public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(PedestalBlockEntity pedestal, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = pedestal.getHeldItem();

        if (stack == null || stack.isEmpty()) return;

        Vec3 pos = pedestal.getCurrentRenderItemPosition(partialTick);

        poseStack.pushPose();
        poseStack.translate(pos.x - pedestal.getBlockPos().getX(), pos.y - pedestal.getBlockPos().getY(), pos.z - pedestal.getBlockPos().getZ());
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(pedestal.getRenderingRotation(partialTick)));

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED,
                getLightLevel(pedestal.getLevel(), pedestal.getBlockPos()), OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, pedestal.getLevel(), 1);

        poseStack.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int blockLight = level.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
