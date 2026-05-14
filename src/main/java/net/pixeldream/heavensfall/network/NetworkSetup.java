package net.pixeldream.heavensfall.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.pixeldream.heavensfall.HeavensFallMod;

@EventBusSubscriber(modid = HeavensFallMod.MODID)
public class NetworkSetup {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(HeavensFallMod.MODID)
                .versioned("1.0");

        registrar.playToClient(
                SyncProgressionPayload.TYPE,
                SyncProgressionPayload.STREAM_CODEC,
                ClientPayloadHandler::handleData
        );

        registrar.playToServer(
                AcceptQuestPayload.TYPE,
                AcceptQuestPayload.STREAM_CODEC,
                AcceptQuestPayload::handle
        );

        registrar.playToClient(
                OpenQuestGuiPayload.TYPE,
                OpenQuestGuiPayload.STREAM_CODEC,
                ClientPayloadHandler::handleGuiOpen
        );

        registrar.playToServer(
                TurnInQuestPayload.TYPE,
                TurnInQuestPayload.STREAM_CODEC,
                TurnInQuestPayload::handle
        );
    }
}