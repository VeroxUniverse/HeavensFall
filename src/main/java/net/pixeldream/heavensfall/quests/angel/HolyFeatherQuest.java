package net.pixeldream.heavensfall.quests.angel;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;

import java.util.List;

public class HolyFeatherQuest implements IQuest {
    @Override public String getId() { return "heavensfall:angel_quest_feathers"; }
    @Override public String getTitle() { return "Lost Feathers"; }
    @Override public String getDescription() { return "Collect 5 Feathers to help repair a celestial wing."; }
    @Override public PlayerProgression.Fraction getTargetFraction() { return PlayerProgression.Fraction.ANGEL; }

    @Override
    public boolean isCompleted(Player player) {
        return player.getInventory().countItem(Items.FEATHER) >= 5;
    }

    @Override
    public List<ItemStack> getRequiredItems() {
        return List.of(new ItemStack(Items.FEATHER, 5));
    }

    @Override public int getAlignmentReward() { return 10; }

    @Override public List<ItemStack> getItemRewards() {
        return List.of(new ItemStack(Items.GOLD_INGOT, 2));
    }
}