package net.pixeldream.heavensfall.event;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.commands.ProgressionCommand;
import net.pixeldream.heavensfall.commands.TradeCommand;
import net.pixeldream.heavensfall.network.AcceptQuestPayload;
import net.pixeldream.heavensfall.network.SyncProgressionPayload;

@EventBusSubscriber(modid = HeavensFallMod.MODID)
public class HFEvents {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        TradeCommand.register(event.getDispatcher());
        ProgressionCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 != 0) return;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            PlayerProgression data = player.getData(HFAttachments.PROGRESSION);
            if (data.activeQuests().isEmpty()) continue;

            PacketDistributor.sendToPlayer(player, new SyncProgressionPayload(
                    data.completedQuests().stream().toList(),
                    data.activeQuests().stream().toList(),
                    data.angelRank(),
                    data.demonRank(),
                    data.alignment(),
                    AcceptQuestPayload.buildProgressMap(player, data)
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgression data = player.getData(HFAttachments.PROGRESSION);
            PacketDistributor.sendToPlayer(player, new SyncProgressionPayload(
                    data.completedQuests().stream().toList(),
                    data.activeQuests().stream().toList(),
                    data.angelRank(),
                    data.demonRank(),
                    data.alignment(),
                    AcceptQuestPayload.buildProgressMap(player, data)
            ));
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerProgression data = player.getData(HFAttachments.PROGRESSION);
            PacketDistributor.sendToPlayer(player, new SyncProgressionPayload(
                    data.completedQuests().stream().toList(),
                    data.activeQuests().stream().toList(),
                    data.angelRank(),
                    data.demonRank(),
                    data.alignment(),
                    AcceptQuestPayload.buildProgressMap(player, data)
            ));
        }
    }
}