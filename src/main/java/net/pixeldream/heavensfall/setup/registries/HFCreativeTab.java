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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HF_TAB_ITEMS = CREATIVE_MODE_TABS.register("hf_tab_items", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.items." + HeavensFallMod.MODID))
            .icon(() -> HFItems.HOLY_GREATSWORD.get().getDefaultInstance())
            .displayItems((parameters, output) -> {

                HFItems.ITEMS.getEntries().forEach(item -> {
                    output.accept(item.get());

                });
            }).build());

    public static void register(net.neoforged.bus.api.IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}