package net.pixeldream.heavensfall.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.attachment.PlayerProgression;

public record OpenQuestGuiPayload(String questId, PlayerProgression.Fraction fraction) implements CustomPacketPayload {
    public static final Type<OpenQuestGuiPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HeavensFallMod.MODID, "open_quest_gui"));

    public static final StreamCodec<FriendlyByteBuf, OpenQuestGuiPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, OpenQuestGuiPayload::questId,
            ByteBufCodecs.INT, p -> p.fraction().ordinal(),
            (id, orbit) -> new OpenQuestGuiPayload(id, PlayerProgression.Fraction.values()[orbit])
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}