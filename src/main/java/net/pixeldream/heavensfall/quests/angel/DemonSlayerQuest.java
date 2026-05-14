package net.pixeldream.heavensfall.quests.angel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType; // Wichtig für gezielte Kills
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;
import java.util.List;

public class DemonSlayerQuest implements IQuest {
    @Override public String getId() { return "heavensfall:angel_quest_slayer"; }
    @Override public String getTitle() { return "Purge the Undead"; }
    @Override public String getDescription() { return "Defeat 10 Zombies to earn celestial favor."; }
    @Override public PlayerProgression.Fraction getTargetFraction() { return PlayerProgression.Fraction.ANGEL; }

    @Override
    public int getCurrentStatValue(Player player) {
        if (player instanceof ServerPlayer sp) {
            return sp.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.ZOMBIE));
        }
        return 0;
    }

    @Override
    public boolean isCompleted(Player player) {
        if (player instanceof ServerPlayer sp) {
            PlayerProgression data = sp.getData(HFAttachments.PROGRESSION);

            int startKills = data.getStartValue(getId());
            int currentKills = sp.getStats().getValue(Stats.ENTITY_KILLED.get(EntityType.ZOMBIE));

            return (currentKills - startKills) >= 10;
        }
        return false;
    }

    @Override public int getRequiredAmount() { return 10; }
    @Override public boolean isCountable() { return true; }
    @Override public int getAlignmentReward() { return 15; }

    @Override public List<ItemStack> getItemRewards() {
        return List.of(new ItemStack(Items.GLOWSTONE_DUST, 4));
    }
}