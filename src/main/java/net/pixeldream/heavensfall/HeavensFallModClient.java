package net.pixeldream.heavensfall;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.pixeldream.heavensfall.client.ArclightArmorRenderer;
import net.pixeldream.heavensfall.client.WingsCurioRenderer;
import net.pixeldream.heavensfall.client.WingsItemRenderer;
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

        CuriosRendererRegistry.register(
                HFItems.ANGEL_WINGS.get(),
                WingsCurioRenderer::new
        );
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {

    }

}
