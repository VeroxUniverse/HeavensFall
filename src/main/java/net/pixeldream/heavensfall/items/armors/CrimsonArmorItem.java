package net.pixeldream.heavensfall.items.armors;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.pixeldream.heavensfall.client.ArmorDispatcher;

public class CrimsonArmorItem extends ArmorItem {

    public final ArmorDispatcher DISPATCHER;

    public CrimsonArmorItem(Type type, Properties properties) {
        super(ArmorMaterials.DIAMOND, type, properties);
        this.DISPATCHER = new ArmorDispatcher();
    }
}
