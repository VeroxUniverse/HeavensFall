package net.pixeldream.heavensfall.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;

public record AcceptQuestPayload(String questId) implements CustomPacketPayload {
    public static final Type<AcceptQuestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "accept_quest"));

    public static final StreamCodec<FriendlyByteBuf, AcceptQuestPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AcceptQuestPayload::questId,
            AcceptQuestPayload::new
    );

    public static java.util.Map<String, Integer> buildProgressMap(ServerPlayer player, PlayerProgression data) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        for (String questId : data.activeQuests()) {
            IQuest quest = QuestManager.getQuests().get(questId);
            if (quest != null) {
                int start = data.questStartValues().getOrDefault(questId, 0);
                int current = quest.getCurrentStatValue(player);
                map.put(questId, current - start);
            }
        }
        return map;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(AcceptQuestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            IQuest quest = QuestManager.getQuests().get(payload.questId());
            if (quest != null) {
                var data = serverPlayer.getData(HFAttachments.PROGRESSION);

                int currentStat = quest.getCurrentStatValue(serverPlayer);

                PlayerProgression updated = data.acceptQuest(quest.getId(), currentStat);
                serverPlayer.setData(HFAttachments.PROGRESSION, updated);

                PacketDistributor.sendToPlayer(serverPlayer, new SyncProgressionPayload(
                        updated.completedQuests().stream().toList(),
                        updated.activeQuests().stream().toList(),
                        updated.angelRank(),
                        updated.demonRank(),
                        updated.alignment(),
                        buildProgressMap(serverPlayer, updated)
                ));

                serverPlayer.displayClientMessage(Component.literal("§6Quest accepted: " + quest.getTitle()), true);
            }
        });
    }
}