package net.pixeldream.heavensfall.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.armors.ArlightArmorItem;

public class HFItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HeavensFallMod.MODID);

    public static final DeferredItem<Item> HOLY_GREATSWORD = ITEMS.register("holy_greatsword",
            () -> new SwordItem(Tiers.DIAMOND, new Item.Properties().
                    attributes(SwordItem.createAttributes(Tiers.DIAMOND, 1,-2.0f))));

    public static final DeferredItem<Item> ANGEL_WINGS = ITEMS.register("angel_wings",
            () -> new AnimatedWingsItem(new Item.Properties().stacksTo(1).durability(1024)));

    public static final DeferredItem<Item> ARCLIGHT_HELMET = ITEMS.register("arclight_helmet",
            () -> new ArlightArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final DeferredItem<Item> ARCLIGHT_CHESPLATE = ITEMS.register("arclight_chestplate",
            () -> new ArlightArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final DeferredItem<Item> ARCLIGHT_LEGGINGS = ITEMS.register("arclight_leggings",
            () -> new ArlightArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final DeferredItem<Item> ARCLIGHT_BOOTS = ITEMS.register("arclight_boots",
            () -> new ArlightArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
