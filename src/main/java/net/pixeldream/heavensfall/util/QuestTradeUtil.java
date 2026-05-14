package net.pixeldream.heavensfall.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.player.Player;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.items.HFItems;

public class QuestTradeUtil {
    public static MerchantOffers getAvailableTrades(Player player) {
        MerchantOffers offers = new MerchantOffers();
        var progression = player.getData(HFAttachments.PROGRESSION);

        // --- COMMON TRADES (Standard) ---
        offers.add(new MerchantOffer(
                new ItemCost(Items.GOLD_INGOT, 32),
                new ItemStack(Items.NETHERITE_SCRAP, 1),
                16, 5, 0.05f));

        // --- DEMON TRADES ---
        if (progression.isCompleted("heavensfall:demon_rank_1")) {
            offers.add(new MerchantOffer(
                    new ItemCost(Items.DIAMOND, 1),
                    new ItemStack(HFItems.DEMONIC_GREATSWORD.get()),
                    3, 15, 0.05f));
        }
        // Hier weitere Demon-Trades einfügen...

        // --- ANGEL TRADES ---
        if (progression.isCompleted("heavensfall:angel_rank_1")) {
            offers.add(new MerchantOffer(
                    new ItemCost(Items.IRON_INGOT, 64),
                    new ItemStack(HFItems.ANGEL_GREATSWORD.get()),
                    5, 10, 0.05f));
        }
        // Hier weitere Angel-Trades einfügen...

        return offers;
    }
}