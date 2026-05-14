package net.pixeldream.heavensfall.quests;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import java.util.List;

public interface IQuest {
    String getId();
    String getTitle();
    String getDescription();
    PlayerProgression.Fraction getTargetFraction();
    boolean isCompleted(Player player);
    int getAlignmentReward();
    List<ItemStack> getItemRewards();

    default List<ItemStack> getRequiredItems() {
        return List.of();
    }
    default int getCurrentStatValue(Player player) { return 0; }
    default int getRequiredAmount() { return 1; }
    default boolean isCountable() { return false; }
}