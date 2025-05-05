package net.pixeldream.heavensfall.items;

import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class WingsItemDispatcher {

    private static final AzCommand OPEN_COMMAND = AzCommand.create(
            "base_controller",
            "unfolding",
            AzPlayBehaviors.HOLD_ON_LAST_FRAME);
    private static final AzCommand CLOSE_COMMAND = AzCommand.create(
            "base_controller",
            "folding",
            AzPlayBehaviors.HOLD_ON_LAST_FRAME);
    private static final AzCommand GLIDE_COMMAND = AzCommand.create(
            "base_controller",
            "glide",
            AzPlayBehaviors.LOOP);
    private static final AzCommand FLY_COMMAND = AzCommand.create(
            "base_controller",
            "fly",
            AzPlayBehaviors.LOOP);
    private static final AzCommand IDLE_COMMAND = AzCommand.create(
            "base_controller",
            "idle",
            AzPlayBehaviors.LOOP);


    public void openWings(Entity entity, ItemStack itemStack) {
        OPEN_COMMAND.sendForItem(entity, itemStack);
    }

    public void closeWings(Entity entity, ItemStack itemStack) {
        CLOSE_COMMAND.sendForItem(entity, itemStack);
    }

    public void glideWings(Entity entity, ItemStack itemStack) {
        GLIDE_COMMAND.sendForItem(entity, itemStack);
    }

    public void flyWings(Entity entity, ItemStack itemStack) {
        FLY_COMMAND.sendForItem(entity, itemStack);
    }

    public void idleWings(Entity entity, ItemStack itemStack) {
        IDLE_COMMAND.sendForItem(entity, itemStack);
    }


}
