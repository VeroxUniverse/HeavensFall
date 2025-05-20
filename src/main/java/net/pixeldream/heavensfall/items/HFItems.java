package net.pixeldream.heavensfall.items;

import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.items.armors.ArlightArmorItem;
import net.pixeldream.heavensfall.items.armors.CrimsonArmorItem;
import net.pixeldream.heavensfall.items.armors.FallenArmorItem;

public class HFItems {
    public static final DeferredRegister.Items ITEMS_ARMORY = DeferredRegister.createItems(HeavensFallMod.MODID);
    public static final DeferredRegister.Items ITEMS_RESOURCES = DeferredRegister.createItems(HeavensFallMod.MODID);
    public static final DeferredRegister.Items ITEMS_BLOCKS = DeferredRegister.createItems(HeavensFallMod.MODID);

    // Weapons //

    public static final DeferredItem<Item> HOLY_GREATSWORD = ITEMS_ARMORY.register("holy_greatsword",
            () -> new SwordItem(Tiers.DIAMOND, new Item.Properties().
                    attributes(SwordItem.createAttributes(Tiers.DIAMOND, 1,-2.0f)).rarity(Rarity.RARE)));

    // Armors //

    public static final DeferredItem<Item> ANGEL_WINGS = ITEMS_ARMORY.register("angel_wings",
            () -> new AnimatedWingsItem(
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).durability(1024).rarity(Rarity.EPIC)
            ));

    public static final DeferredItem<Item> ARCLIGHT_HELMET = ITEMS_ARMORY.register("arclight_helmet",
            () -> new ArlightArmorItem(
                    ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> ARCLIGHT_CHESPLATE = ITEMS_ARMORY.register("arclight_chestplate",
            () -> new ArlightArmorItem(
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> ARCLIGHT_LEGGINGS = ITEMS_ARMORY.register("arclight_leggings",
            () -> new ArlightArmorItem(
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> ARCLIGHT_BOOTS = ITEMS_ARMORY.register("arclight_boots",
            () -> new ArlightArmorItem(
                    ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> FALLEN_HELMET = ITEMS_ARMORY.register("fallen_helmet",
            () -> new FallenArmorItem(
                    ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> FALLEN_CHESPLATE = ITEMS_ARMORY.register("fallen_chestplate",
            () -> new FallenArmorItem(
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> FALLEN_LEGGINGS = ITEMS_ARMORY.register("fallen_leggings",
            () -> new FallenArmorItem(
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> FALLEN_BOOTS = ITEMS_ARMORY.register("fallen_boots",
            () -> new FallenArmorItem(
                    ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> CRIMSON_HELMET = ITEMS_ARMORY.register("crimson_helmet",
            () -> new CrimsonArmorItem(
                    ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> CRIMSON_CHESPLATE = ITEMS_ARMORY.register("crimson_chestplate",
            () -> new CrimsonArmorItem(
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> CRIMSON_LEGGINGS = ITEMS_ARMORY.register("crimson_leggings",
            () -> new CrimsonArmorItem(
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));
    public static final DeferredItem<Item> CRIMSON_BOOTS = ITEMS_ARMORY.register("crimson_boots",
            () -> new CrimsonArmorItem(
                    ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).durability(ArmorItem.Type.BOOTS.getDurability(33))
            ));

    // Resources //


    public static void registerArmory(IEventBus eventBus) {
        ITEMS_ARMORY.register(eventBus);
    }
    public static void registerResources(IEventBus eventBus) {
        ITEMS_RESOURCES.register(eventBus);
    }
    public static void registerBlocks(IEventBus eventBus) {
        ITEMS_BLOCKS.register(eventBus);
    }

    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
