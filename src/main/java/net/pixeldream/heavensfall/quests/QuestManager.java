package net.pixeldream.heavensfall.quests;

import net.pixeldream.heavensfall.quests.angel.DemonSlayerQuest;
import net.pixeldream.heavensfall.quests.angel.HolyFeatherQuest;
import net.pixeldream.heavensfall.quests.demon.SoulsOfInnocentsQuest;
import net.pixeldream.heavensfall.quests.fallen.IntoTheVoidQuest;

import java.util.HashMap;
import java.util.Map;

public class QuestManager {
    private static final Map<String, IQuest> REGISTERED_QUESTS = new HashMap<>();

    public static void register(IQuest quest) {
        REGISTERED_QUESTS.put(quest.getId(), quest);
    }

    public static Map<String, IQuest> getQuests() {
        return REGISTERED_QUESTS;
    }

    public static void init() {
        register(new HolyFeatherQuest());
        register(new DemonSlayerQuest());
        register(new IntoTheVoidQuest());
        register(new SoulsOfInnocentsQuest());
    }
}