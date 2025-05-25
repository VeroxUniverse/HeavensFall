package net.pixeldream.heavensfall;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pixeldream.heavensfall.blocks.blockentity.*;
import net.pixeldream.heavensfall.client.*;
import net.pixeldream.heavensfall.items.HFItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(modid = HeavensFallMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HeavensFallModClient {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

        AzArmorRendererRegistry.register(
                WingsItemRenderer::new,
                HFItems.ANGEL_WINGS.get()

        );
        AzArmorRendererRegistry.register(
                ArclightArmorRenderer::new,
                HFItems.ARCLIGHT_HELMET.get(),
                HFItems.ARCLIGHT_CHESPLATE.get(),
                HFItems.ARCLIGHT_LEGGINGS.get(),
                HFItems.ARCLIGHT_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
                FallenKnightArmorRenderer::new,
                HFItems.FALLEN_HELMET.get(),
                HFItems.FALLEN_CHESPLATE.get(),
                HFItems.FALLEN_LEGGINGS.get(),
                HFItems.FALLEN_BOOTS.get()
        );
        AzArmorRendererRegistry.register(
                CrimsonArmorRenderer::new,
                HFItems.CRIMSON_HELMET.get(),
                HFItems.CRIMSON_CHESPLATE.get(),
                HFItems.CRIMSON_LEGGINGS.get(),
                HFItems.CRIMSON_BOOTS.get()
        );

        CuriosRendererRegistry.register(
                HFItems.ANGEL_WINGS.get(),
                WingsCurioRenderer::new
        );
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HFBlockEntities.PEDESTAL_ENTITY.get(), PedestalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.ALTAR_ENTITY.get(), AltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.PEDESTAL_TABLE_ENTITY.get(), PedestalTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.ALTAR_PILLAR_ENTITY.get(), AltarPillarBlockEntityRenderer::new);
    }

}
