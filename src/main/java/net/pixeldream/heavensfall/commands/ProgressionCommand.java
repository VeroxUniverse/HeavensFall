package net.pixeldream.heavensfall.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.network.AcceptQuestPayload;
import net.pixeldream.heavensfall.network.OpenQuestGuiPayload;
import net.pixeldream.heavensfall.network.SyncProgressionPayload;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ProgressionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hfprogression")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("alignment")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("value", IntegerArgumentType.integer(-100, 100))
                                        .executes(context -> {
                                            int value = IntegerArgumentType.getInteger(context, "value");
                                            return updateProgression(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                                                    p -> p.addAlignment(value - p.alignment()));
                                        }))))
                .then(Commands.literal("quest")
                        .then(Commands.literal("open_angel")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(context -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : targets) {
                                                PacketDistributor.sendToPlayer(player, new OpenQuestGuiPayload("", PlayerProgression.Fraction.ANGEL));
                                            }
                                            return targets.size();
                                        })
                                        .then(Commands.argument("questId", StringArgumentType.string())
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "questId");
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                                    for (ServerPlayer player : targets) {
                                                        PacketDistributor.sendToPlayer(player, new OpenQuestGuiPayload(id, PlayerProgression.Fraction.ANGEL));
                                                    }
                                                    return targets.size();
                                                }))))
                        .then(Commands.literal("open_demon")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .executes(context -> {
                                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                            for (ServerPlayer player : targets) {
                                                PacketDistributor.sendToPlayer(player, new OpenQuestGuiPayload("", PlayerProgression.Fraction.DEMON));
                                            }
                                            return targets.size();
                                        })
                                        .then(Commands.argument("questId", StringArgumentType.string())
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "questId");
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                                    for (ServerPlayer player : targets) {
                                                        PacketDistributor.sendToPlayer(player, new OpenQuestGuiPayload(id, PlayerProgression.Fraction.DEMON));
                                                    }
                                                    return targets.size();
                                                }))))
                        .then(Commands.literal("accept")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("questId", StringArgumentType.string())
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "questId");
                                                    Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
                                                    IQuest quest = QuestManager.getQuests().get(id);
                                                    if (quest == null) {
                                                        context.getSource().sendFailure(Component.literal("Quest ID nicht gefunden: " + id));
                                                        return 0;
                                                    }
                                                    for (ServerPlayer player : targets) {
                                                        int startValue = quest.getCurrentStatValue(player);
                                                        updateProgression(context.getSource(), java.util.List.of(player),
                                                                p -> p.acceptQuest(id, startValue));
                                                    }
                                                    return targets.size();
                                                }))))
                        .then(Commands.literal("complete")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("questId", StringArgumentType.string())
                                                .executes(context -> {
                                                    String id = StringArgumentType.getString(context, "questId");
                                                    return updateProgression(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                                                            p -> p.completeQuest(id));
                                                }))))
                )
                .then(Commands.literal("rank")
                        .then(Commands.literal("angel")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(context -> updateProgression(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                                                        p -> p.withAngelRank(StringArgumentType.getString(context, "name")))))))
                        .then(Commands.literal("demon")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .executes(context -> updateProgression(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                                                        p -> p.withDemonRank(StringArgumentType.getString(context, "name"))))))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> updateProgression(context.getSource(), EntityArgument.getPlayers(context, "targets"),
                                        p -> new PlayerProgression(new HashSet<>(), new HashSet<>(), new HashMap<>(), "Novice", "Initiate", 0).updateTitles())))
                )
        );
    }

    private static int updateProgression(CommandSourceStack source, Collection<ServerPlayer> targets, java.util.function.Function<PlayerProgression, PlayerProgression> updateFunc) {
        for (ServerPlayer player : targets) {
            PlayerProgression current = player.getData(HFAttachments.PROGRESSION);
            PlayerProgression updated = updateFunc.apply(current);
            player.setData(HFAttachments.PROGRESSION, updated);
            sync(player, updated);
        }
        source.sendSuccess(() -> Component.literal("§aProgression aktualisiert und synchronisiert."), true);
        return targets.size();
    }

    private static void sync(ServerPlayer player, PlayerProgression data) {
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