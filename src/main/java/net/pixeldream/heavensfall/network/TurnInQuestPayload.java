package net.pixeldream.heavensfall.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;

public record TurnInQuestPayload(String questId) implements CustomPacketPayload {
    public static final Type<TurnInQuestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "turn_in_quest")
    );

    public static final StreamCodec<FriendlyByteBuf, TurnInQuestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TurnInQuestPayload::questId,
            TurnInQuestPayload::new
    );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(TurnInQuestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            IQuest quest = QuestManager.getQuests().get(payload.questId());
            if (quest == null) return;

            PlayerProgression data = serverPlayer.getData(HFAttachments.PROGRESSION);

            if (!data.activeQuests().contains(quest.getId())) return;

            if (!quest.isCompleted(serverPlayer)) {
                serverPlayer.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§cQuest not completed yet!"), true
                );
                return;
            }

            for (ItemStack cost : quest.getRequiredItems()) {
                int needed = cost.getCount();
                for (int i = 0; i < serverPlayer.getInventory().getContainerSize(); i++) {
                    ItemStack slot = serverPlayer.getInventory().getItem(i);
                    if (slot.getItem() == cost.getItem()) {
                        int take = Math.min(slot.getCount(), needed);
                        slot.shrink(take);
                        needed -= take;
                        if (needed <= 0) break;
                    }
                }
            }

            PlayerProgression updated = data.recycleQuest(quest.getId()).addAlignment(quest.getAlignmentReward());
            serverPlayer.setData(HFAttachments.PROGRESSION, updated);

            for (ItemStack reward : quest.getItemRewards()) {
                if (!serverPlayer.addItem(reward.copy())) {
                    serverPlayer.drop(reward.copy(), false);
                }
            }

            serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§aQuest completed: " + quest.getTitle()), false
            );

            PacketDistributor.sendToPlayer(serverPlayer, new SyncProgressionPayload(
                    updated.completedQuests().stream().toList(),
                    updated.activeQuests().stream().toList(),
                    updated.angelRank(),
                    updated.demonRank(),
                    updated.alignment(),
                    AcceptQuestPayload.buildProgressMap(serverPlayer, updated)
            ));
        });
    }
}