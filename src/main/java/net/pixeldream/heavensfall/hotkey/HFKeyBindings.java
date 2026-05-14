package net.pixeldream.heavensfall.hotkey;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class HFKeyBindings {
    public static final KeyMapping QUEST_LOG_KEY = new KeyMapping(
            "key.heavensfall.quest_log",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.heavensfall"
    );
}