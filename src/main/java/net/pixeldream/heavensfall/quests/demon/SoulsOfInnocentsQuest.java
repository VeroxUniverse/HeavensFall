package net.pixeldream.heavensfall.quests.demon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;
import java.util.List;

public class SoulsOfInnocentsQuest implements IQuest {
    @Override public String getId() { return "heavensfall:demon_quest_souls"; }
    @Override public String getTitle() { return "Souls of the Weak"; }
    @Override public String getDescription() { return "Slay 5 Villagers to feed the abyss."; }
    @Override public PlayerProgression.Fraction getTargetFraction() { return PlayerProgression.Fraction.DEMON; }

    @Override
    public int getCurrentStatValue(Player player) {
        if (player instanceof ServerPlayer sp) {
            return sp.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.VILLAGER));
        }
        return 0;
    }

    @Override
    public boolean isCompleted(Player player) {
        if (player instanceof ServerPlayer sp) {
            PlayerProgression data = sp.getData(HFAttachments.PROGRESSION);

            int startKills = data.getStartValue(getId());
            int currentKills = sp.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.VILLAGER));

            return (currentKills - startKills) >= 5;
        }
        return false;
    }

    @Override public int getRequiredAmount() { return 5; }
    @Override public boolean isCountable() { return true; }
    @Override public int getAlignmentReward() { return -20; }
    @Override public List<ItemStack> getItemRewards() {
        return List.of(new ItemStack(Items.WITHER_SKELETON_SKULL, 1));
    }
}