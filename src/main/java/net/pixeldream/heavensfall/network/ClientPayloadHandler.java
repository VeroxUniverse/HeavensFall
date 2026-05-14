package net.pixeldream.heavensfall.network;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.client.gui.QuestAcceptScreen;
import net.pixeldream.heavensfall.client.gui.QuestBoardScreen;
import net.pixeldream.heavensfall.quests.QuestManager;

import java.util.HashSet;

public class ClientPayloadHandler {

    public static java.util.Map<String, Integer> clientQuestProgress = new java.util.HashMap<>();

    public static void handleGuiOpen(final OpenQuestGuiPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (data.questId().isEmpty()) {
                Minecraft.getInstance().setScreen(new QuestBoardScreen(data.fraction()));
            } else {
                var quest = QuestManager.getQuests().get(data.questId());
                if (quest != null) {
                    Minecraft.getInstance().setScreen(new QuestAcceptScreen(quest, data.fraction()));
                }
            }
        });
    }

    public static void handleData(final SyncProgressionPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();
            clientQuestProgress = data.questProgress();

            PlayerProgression updated = new PlayerProgression(
                    new HashSet<>(data.completedQuests()),
                    new HashSet<>(data.activeQuests()),
                    new java.util.HashMap<>(),
                    data.angelRank(),
                    data.demonRank(),
                    data.alignment()
            );
            player.setData(HFAttachments.PROGRESSION, updated);

            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.screen instanceof net.pixeldream.heavensfall.client.gui.QuestLogScreen screen) {
                screen.rebuildAllWidgets();
            }
        });
    }
}