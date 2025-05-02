package net.pixeldream.heavensfall.setup.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.Heavensfall;

public class HFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Heavensfall.MODID);

    public static final DeferredItem<Item> HOLY_GREATSWORD = ITEMS.register("holy_greatsword",
            () -> new SwordItem(Tiers.DIAMOND, new Item.Properties().
                    attributes(SwordItem.createAttributes(Tiers.DIAMOND, 1,-2.0f))));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
