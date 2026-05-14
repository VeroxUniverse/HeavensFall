package net.pixeldream.heavensfall.items;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.event.FractionEvents; // Import für die sync Methode

public abstract class AbstractQuestItem extends Item {
    public AbstractQuestItem(Properties properties) { super(properties); }

    protected abstract String getQuestId();
    protected abstract int getAlignmentChange();
    protected abstract String getUnlockMessage();
    protected abstract boolean isPact();

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PlayerProgression progression = serverPlayer.getData(HFAttachments.PROGRESSION);

            if (isPact() && progression.isCompleted(getQuestId())) {
                serverPlayer.displayClientMessage(Component.literal("§cYou already hold this pact!"), true);
                return InteractionResultHolder.fail(itemstack);
            }

            PlayerProgression updated = progression.addAlignment(getAlignmentChange());
            updated = updated.addQuest(getQuestId());
            serverPlayer.setData(HFAttachments.PROGRESSION, updated);
            FractionEvents.sync(serverPlayer, updated);
            serverPlayer.displayClientMessage(Component.literal(getUnlockMessage()), false);

            if (!serverPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}