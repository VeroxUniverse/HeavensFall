package net.pixeldream.heavensfall;

import com.mojang.logging.LogUtils;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererRegistry;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.pixeldream.heavensfall.client.WingsCurioRenderer;
import net.pixeldream.heavensfall.client.WingsItemRenderer;
import net.pixeldream.heavensfall.setup.registries.HFCreativeTab;
import net.pixeldream.heavensfall.items.HFItems;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(HeavensFallMod.MODID)
public class HeavensFallMod {

    public static final String MODID = "heavensfall";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HeavensFallMod(IEventBus modEventBus, ModContainer modContainer) {
        AzureLib.initialize();
        HFItems.register(modEventBus);
        HFCreativeTab.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        AzIdentityRegistry.register(
                HFItems.ANGEL_WINGS.get(),
                HFItems.ARCLIGHT_HELMET.get(),
                HFItems.ARCLIGHT_CHESPLATE.get(),
                HFItems.ARCLIGHT_LEGGINGS.get(),
                HFItems.ARCLIGHT_BOOTS.get()
        );
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("HELLO FROM CLIENT SETUP");

        }
    }
}
