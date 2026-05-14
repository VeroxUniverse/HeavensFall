package net.pixeldream.heavensfall.attachment;

import net.pixeldream.heavensfall.HeavensFallMod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

public class HFAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, HeavensFallMod.MODID);

    public static final Supplier<AttachmentType<PlayerProgression>> PROGRESSION =
            ATTACHMENT_TYPES.register("progression", () -> AttachmentType.builder(() ->
                            new PlayerProgression(
                                    new HashSet<>(),    // completedQuests
                                    new HashSet<>(),    // activeQuests
                                    new HashMap<>(),    // questStartValues
                                    "Novice",           // angelRank
                                    "Initiate",         // demonRank
                                    0                   // alignment
                            ))
                    .serialize(PlayerProgression.CODEC)
                    .copyOnDeath()
                    .build());
}