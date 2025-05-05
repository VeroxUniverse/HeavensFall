package net.pixeldream.heavensfall.hotkey;

import com.mojang.blaze3d.platform.InputConstants;
import cpw.mods.util.Lazy;
import net.minecraft.client.KeyMapping;
import net.pixeldream.heavensfall.HeavensFallMod;
import org.lwjgl.glfw.GLFW;

public class Hotkeys {

    public static final Lazy<KeyMapping> FLY_MAPPING = Lazy.of(() ->
            new KeyMapping(
                    "key." + HeavensFallMod.MODID + ".fly" ,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_L,
                    "key.categories.movement"
            ));

}
