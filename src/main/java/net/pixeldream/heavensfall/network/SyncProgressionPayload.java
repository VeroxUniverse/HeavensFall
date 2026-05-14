package net.pixeldream.heavensfall.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;

import java.util.List;

public record SyncProgressionPayload(
        List<String> completedQuests,
        List<String> activeQuests,
        String angelRank,
        String demonRank,
        int alignment,
        java.util.Map<String, Integer> questProgress  // NEU: berechneter Progress
) implements CustomPacketPayload {

    public static final Type<SyncProgressionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "sync_progression"));

    public static final StreamCodec<FriendlyByteBuf, SyncProgressionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncProgressionPayload::completedQuests,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncProgressionPayload::activeQuests,
            ByteBufCodecs.STRING_UTF8, SyncProgressionPayload::angelRank,
            ByteBufCodecs.STRING_UTF8, SyncProgressionPayload::demonRank,
            ByteBufCodecs.INT, SyncProgressionPayload::alignment,
            ByteBufCodecs.map(java.util.HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.INT),
            SyncProgressionPayload::questProgress,
            SyncProgressionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}