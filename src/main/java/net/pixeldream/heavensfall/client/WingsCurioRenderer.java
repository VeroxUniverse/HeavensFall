package net.pixeldream.heavensfall.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.rewrite.model.AzBakedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class WingsCurioRenderer implements ICurioRenderer {

    private final WingsItemRenderer wingsRenderer = new WingsItemRenderer();

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack matrixStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        LivingEntity entity = slotContext.entity();

        matrixStack.pushPose();

        matrixStack.translate(0, 0.5, 0.25);

        wingsRenderer.prepForRender(entity, stack, EquipmentSlot.CHEST, (HumanoidModel<?>) renderLayerParent.getModel());

        AzBakedModel model = wingsRenderer.provider().provideBakedModel(stack);
        ResourceLocation textureLocation = WingsItemRenderer.TEX;
        RenderType renderType = RenderType.entityCutout(textureLocation);
        VertexConsumer buffer = renderTypeBuffer.getBuffer(renderType);

        wingsRenderer.rendererPipeline().render(matrixStack, model, stack, renderTypeBuffer, renderType, buffer, netHeadYaw, partialTicks, light);

        matrixStack.popPose();

    }
}
