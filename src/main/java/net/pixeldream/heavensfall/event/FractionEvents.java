package net.pixeldream.heavensfall.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.HFAttachments;
import net.pixeldream.heavensfall.attachment.PlayerProgression;
import net.pixeldream.heavensfall.network.AcceptQuestPayload;
import net.pixeldream.heavensfall.network.SyncProgressionPayload;
import net.pixeldream.heavensfall.quests.IQuest;
import net.pixeldream.heavensfall.quests.QuestManager;
import net.pixeldream.heavensfall.util.IFractionBound;

@EventBusSubscriber(modid = HeavensFallMod.MODID)
public class FractionEvents {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        var data = player.getData(HFAttachments.PROGRESSION);
        PlayerProgression.Fraction fraction = data.getFraction();

        if (fraction == PlayerProgression.Fraction.DEMON) {
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false));
        }

        if (fraction == PlayerProgression.Fraction.ANGEL) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    public static void sync(Player player, PlayerProgression data) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        PacketDistributor.sendToPlayer(serverPlayer, new SyncProgressionPayload(
                data.completedQuests().stream().toList(),
                data.activeQuests().stream().toList(),
                data.angelRank(),
                data.demonRank(),
                data.alignment(),
                AcceptQuestPayload.buildProgressMap(serverPlayer, data)
        ));
    }

    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (event.getItemStack().getItem() instanceof IFractionBound bound) {
            var data = player.getData(HFAttachments.PROGRESSION);
            if (data.getFraction() != bound.getRequiredFraction()) {
                event.setCanceled(true);
                if (player.level().isClientSide) {
                    player.displayClientMessage(Component.literal("§cYou cannot wield this power!"), true);
                }
            }
        }
    }
}