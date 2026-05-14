package net.pixeldream.heavensfall.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.pixeldream.heavensfall.HeavensFallMod;
import net.pixeldream.heavensfall.client.gui.QuestLogScreen;
import net.pixeldream.heavensfall.hotkey.HFKeyBindings;

@EventBusSubscriber(modid = HeavensFallMod.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (HFKeyBindings.QUEST_LOG_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new QuestLogScreen());
        }
    }
}
