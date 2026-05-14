package net.pixeldream.heavensfall.quests.fallen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;

import java.util.List;

public class IntoTheVoidQuest implements IQuest {
    @Override public String getId() { return "heavensfall:fallen_quest_void"; }
    @Override public String getTitle() { return "Touching the Stars"; }
    @Override public String getDescription() { return "Climb to the highest peaks (Y = 200)."; }
    @Override public PlayerProgression.Fraction getTargetFraction() { return PlayerProgression.Fraction.FALLEN; }

    @Override
    public boolean isCompleted(Player player) {
        return player.getY() >= 200;
    }

    @Override public int getAlignmentReward() { return 0; } // Neutral

    @Override public List<ItemStack> getItemRewards() {
        return List.of(new ItemStack(Items.ENDER_PEARL, 1));
    }
}