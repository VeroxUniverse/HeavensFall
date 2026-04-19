package net.pixeldream.heavensfall;

import mod.azure.azurelib.common.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.common.render.item.AzItemRendererRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pixeldream.heavensfall.blocks.blockentity.*;
import net.pixeldream.heavensfall.client.*;
import net.pixeldream.heavensfall.items.HFItems;
import net.pixeldream.heavensfall.items.weapons.AngelSwordRenderer;
import net.pixeldream.heavensfall.items.weapons.DemonSwordRenderer;
import net.pixeldream.heavensfall.items.weapons.FallenSwordRenderer;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(modid = HeavensFallMod.MODID, value = Dist.CLIENT)
public class HeavensFallModClient {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

        AzItemRendererRegistry.register(DemonSwordRenderer::new, HFItems.DEMONIC_GREATSWORD.get());
        AzItemRendererRegistry.register(FallenSwordRenderer::new, HFItems.FALLEN_GREATSWORD.get());
        AzItemRendererRegistry.register(AngelSwordRenderer::new, HFItems.ANGEL_GREATSWORD.get());

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
                HolyKnightArmorRenderer::new,
                HFItems.HOLY_KNIGHT_HELMET.get(),
                HFItems.HOLY_KNIGHT_CHESPLATE.get(),
                HFItems.HOLY_KNIGHT_LEGGINGS.get(),
                HFItems.HOLY_KNIGHT_BOOTS.get()
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
        event.registerBlockEntityRenderer(HFBlockEntities.PEDESTAL_ENTITY.get(), DemonPedestalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.ALTAR_ENTITY.get(), DemonAltarBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.PEDESTAL_TABLE_ENTITY.get(), AngelPedestalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(HFBlockEntities.ALTAR_PILLAR_ENTITY.get(), AngelAltarBlockEntityRenderer::new);
    }

}
