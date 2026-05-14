package net.pixeldream.heavensfall.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.pixeldream.heavensfall.util.QuestTradeUtil;
import javax.annotation.Nullable;
import java.util.OptionalInt;

public class TradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hftrades")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    MerchantOffers offers = QuestTradeUtil.getAvailableTrades(player);

                    if (offers.isEmpty()) {
                        player.sendSystemMessage(Component.literal("§cYou haven't unlocked any celestial trades yet!"));
                        return 0;
                    }

                    Merchant merchant = new Merchant() {
                        private Player tradingPlayer;
                        @Override public void setTradingPlayer(@Nullable Player p) { this.tradingPlayer = p; }
                        @Nullable @Override public Player getTradingPlayer() { return this.tradingPlayer != null ? this.tradingPlayer : player; }
                        @Override public MerchantOffers getOffers() { return offers; }
                        @Override public void overrideOffers(MerchantOffers o) {}
                        @Override public void notifyTrade(MerchantOffer o) {}
                        @Override public void notifyTradeUpdated(ItemStack s) {}
                        @Override public int getVillagerXp() { return 0; }
                        @Override public void overrideXp(int xp) {}
                        @Override public boolean showProgressBar() { return false; }
                        @Override public net.minecraft.sounds.SoundEvent getNotifyTradeSound() { return net.minecraft.sounds.SoundEvents.VILLAGER_YES; }
                        @Override public boolean isClientSide() { return false; }
                    };

                    OptionalInt menuId = player.openMenu(new SimpleMenuProvider((id, inv, p) ->
                            new MerchantMenu(id, inv, merchant), Component.literal("Celestial Ritual Trades")));

                    if (menuId.isPresent()) {
                        player.sendMerchantOffers(menuId.getAsInt(), offers, 1, 0, false, false);
                    }

                    return 1;
                })
        );
    }
}