package net.pixeldream.heavensfall;

import com.mojang.logging.LogUtils;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.pixeldream.heavensfall.blocks.HFBlocks;
import net.pixeldream.heavensfall.blocks.blockentity.HFBlockEntities;
import net.pixeldream.heavensfall.hotkey.Hotkeys;
import net.pixeldream.heavensfall.recipes.ritual.RitualRecipeManager;
import net.pixeldream.heavensfall.setup.registries.HFCreativeTab;
import net.pixeldream.heavensfall.items.HFItems;
import org.slf4j.Logger;

@Mod(HeavensFallMod.MODID)
public class HeavensFallMod {

    public static final String MODID = "heavensfall";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HeavensFallMod(IEventBus modEventBus, ModContainer modContainer) {
        AzureLib.initialize();
        HFItems.registerBlocks(modEventBus);
        HFItems.registerArmory(modEventBus);
        HFItems.registerResources(modEventBus);
        HFBlocks.register(modEventBus);
        HFBlockEntities.register(modEventBus);
        HFCreativeTab.register(modEventBus);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
//      NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::commonSetup);
    }

    @SubscribeEvent
    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(Hotkeys.FLY_MAPPING.get());
    }

    public void commonSetup(final FMLCommonSetupEvent event) {

        LOGGER.info("HELLO FROM COMMON SETUP");
        AzIdentityRegistry.register(
                HFItems.ANGEL_WINGS.get(),
                HFItems.ARCLIGHT_HELMET.get(),
                HFItems.ARCLIGHT_CHESPLATE.get(),
                HFItems.ARCLIGHT_LEGGINGS.get(),
                HFItems.ARCLIGHT_BOOTS.get(),
                HFItems.FALLEN_HELMET.get(),
                HFItems.FALLEN_CHESPLATE.get(),
                HFItems.FALLEN_LEGGINGS.get(),
                HFItems.FALLEN_BOOTS.get(),
                HFItems.CRIMSON_HELMET.get(),
                HFItems.CRIMSON_CHESPLATE.get(),
                HFItems.CRIMSON_LEGGINGS.get(),
                HFItems.CRIMSON_BOOTS.get()
        );
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new RitualRecipeManager());
    }

}
