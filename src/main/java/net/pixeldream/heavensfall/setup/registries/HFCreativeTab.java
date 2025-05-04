package net.pixeldream.heavensfall.setup.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.HFItems;

public class HFCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HeavensFallMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HF_TAB_RESOURCE = CREATIVE_MODE_TABS.register("hf_tab_resource", () -> CreativeModeTab.builder()
            .title(Component.translatable(HeavensFallMod.MODID + ".itemGroup.resources"))
            .icon(() -> HFItems.HOLY_GREATSWORD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {

                HFItems.ITEMS_RESOURCES.getEntries().forEach(item -> {
                    output.accept(item.get());

                });
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HF_TAB_AMRORY = CREATIVE_MODE_TABS.register("hf_tab_armory", () -> CreativeModeTab.builder()
            .title(Component.translatable(HeavensFallMod.MODID + ".itemGroup.armory"))
            .icon(() -> HFItems.HOLY_GREATSWORD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {

                HFItems.ITEMS_ARMORY.getEntries().forEach(item -> {
                    output.accept(item.get());

                });
            }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HF_TAB_BLOCKS = CREATIVE_MODE_TABS.register("hf_tab_blocks", () -> CreativeModeTab.builder()
            .title(Component.translatable(HeavensFallMod.MODID + ".itemGroup.blocks"))
            .icon(() -> HFItems.HOLY_GREATSWORD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {

                HFItems.ITEMS_BLOCKS.getEntries().forEach(item -> {
                    output.accept(item.get());

                });
            }).build());

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}